package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.common.enums.ContractStatus;
import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.dto.request.CreateInvoiceRequest;
import com.kma.lamphoun.room_management.dto.request.MarkPaidRequest;
import com.kma.lamphoun.room_management.dto.response.InvoiceResponse;
import com.kma.lamphoun.room_management.entity.Contract;
import com.kma.lamphoun.room_management.entity.Invoice;
import com.kma.lamphoun.room_management.entity.MeterReading;
import com.kma.lamphoun.room_management.entity.Room;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.BadRequestException;
import com.kma.lamphoun.room_management.exception.ForbiddenException;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.repository.ContractRepository;
import com.kma.lamphoun.room_management.repository.InvoiceRepository;
import com.kma.lamphoun.room_management.repository.MeterReadingRepository;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.service.InvoiceService;
import com.kma.lamphoun.room_management.websocket.WebSocketEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final UserRepository userRepository;
    private final WebSocketEventService wsEventService;

    @Override
    @Transactional
    public InvoiceResponse create(String landlordUsername, CreateInvoiceRequest request) {
        User landlord = findUser(landlordUsername);
        Contract contract = findContract(request.getContractId());

        // Chỉ landlord sở hữu contract mới tạo được
        if (!contract.getLandlord().getId().equals(landlord.getId())) {
            throw new ForbiddenException("You do not own this contract");
        }

        // Contract phải ACTIVE
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BadRequestException("Cannot create invoice for a non-ACTIVE contract");
        }

        // Chống trùng invoice cùng contract + tháng
        if (invoiceRepository.existsByContractIdAndBillingMonth(contract.getId(), request.getBillingMonth())) {
            throw new BadRequestException(
                    "Invoice for contract " + contract.getId()
                    + " in month " + request.getBillingMonth() + " already exists");
        }

        // Lấy meter reading và validate thuộc đúng phòng + đúng tháng
        MeterReading meter = findMeterReading(request.getMeterReadingId());
        if (!meter.getRoom().getId().equals(contract.getRoom().getId())) {
            throw new BadRequestException("Meter reading does not belong to the contract's room");
        }
        if (!meter.getBillingMonth().equals(request.getBillingMonth())) {
            throw new BadRequestException(
                    "Meter reading billing month (" + meter.getBillingMonth()
                    + ") does not match invoice billing month (" + request.getBillingMonth() + ")");
        }

        Room room = contract.getRoom();

        // --- Tính từng khoản ---
        BigDecimal rentAmount = contract.getMonthlyRent();

        double electricUsage = meter.getElectricUsage();
        BigDecimal electricPrice = room.getElecPrice();
        BigDecimal electricAmount = electricPrice
                .multiply(BigDecimal.valueOf(electricUsage))
                .setScale(2, RoundingMode.HALF_UP);

        double waterUsage = meter.getWaterUsage();
        BigDecimal waterPrice = room.getWaterPrice();
        BigDecimal waterAmount = waterPrice
                .multiply(BigDecimal.valueOf(waterUsage))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal serviceAmount = room.getServicePrice();

        BigDecimal totalAmount = rentAmount
                .add(electricAmount)
                .add(waterAmount)
                .add(serviceAmount);

        // Hạn thanh toán: cuối tháng billing nếu không truyền
        LocalDate dueDate = request.getDueDate() != null
                ? request.getDueDate()
                : YearMonth.parse(request.getBillingMonth()).atEndOfMonth();

        Invoice invoice = Invoice.builder()
                .contract(contract)
                .meterReading(meter)
                .billingMonth(request.getBillingMonth())
                .rentAmount(rentAmount)
                .electricUsage(electricUsage)
                .electricPrice(electricPrice)
                .electricAmount(electricAmount)
                .waterUsage(waterUsage)
                .waterPrice(waterPrice)
                .waterAmount(waterAmount)
                .serviceAmount(serviceAmount)
                .totalAmount(totalAmount)
                .dueDate(dueDate)
                .note(request.getNote())
                .build();

        InvoiceResponse response = toResponse(invoiceRepository.save(invoice));

        // Push realtime đến tenant
        wsEventService.pushInvoiceCreated(contract.getTenant().getUsername(), response);

        return response;
    }

    @Override
    public InvoiceResponse getById(Long id, String username) {
        Invoice invoice = findInvoice(id);
        checkViewPermission(invoice, username);
        return toResponse(invoice);
    }

    @Override
    public Page<InvoiceResponse> getByLandlord(String landlordUsername, InvoiceStatus status,
                                                Long contractId, Pageable pageable) {
        User landlord = findUser(landlordUsername);
        return invoiceRepository.findByLandlordId(landlord.getId(), status, contractId, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<InvoiceResponse> getByTenant(String tenantUsername, InvoiceStatus status, Pageable pageable) {
        User tenant = findUser(tenantUsername);
        return invoiceRepository.findByTenantId(tenant.getId(), status, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<InvoiceResponse> getByContract(Long contractId, String username, Pageable pageable) {
        Contract contract = findContract(contractId);
        User user = findUser(username);

        // Chỉ landlord của contract hoặc tenant của contract mới xem được
        boolean isLandlord = contract.getLandlord().getId().equals(user.getId());
        boolean isTenant   = contract.getTenant().getId().equals(user.getId());
        if (!isLandlord && !isTenant) {
            throw new ForbiddenException("You do not have access to this contract's invoices");
        }

        return invoiceRepository.findByContractIdOrderByBillingMonthDesc(contractId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public InvoiceResponse markPaid(Long id, String landlordUsername, MarkPaidRequest request) {
        Invoice invoice = findInvoice(id);

        if (!invoice.getContract().getLandlord().getUsername().equals(landlordUsername)) {
            throw new ForbiddenException("You do not have permission to mark this invoice as paid");
        }
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Invoice is already paid");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(request.getPaidAt());
        if (request.getNote() != null) invoice.setNote(request.getNote());

        InvoiceResponse response = toResponse(invoiceRepository.save(invoice));

        // Push realtime đến tenant
        wsEventService.pushInvoicePaid(invoice.getContract().getTenant().getUsername(), response);

        return response;
    }

    // --- helpers ---

    private Invoice findInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    private Contract findContract(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
    }

    private MeterReading findMeterReading(Long id) {
        return meterReadingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter reading not found with id: " + id));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    /** Tenant chỉ xem invoice của mình, landlord chỉ xem invoice của phòng mình */
    private void checkViewPermission(Invoice invoice, String username) {
        Contract contract = invoice.getContract();
        boolean isLandlord = contract.getLandlord().getUsername().equals(username);
        boolean isTenant   = contract.getTenant().getUsername().equals(username);
        if (!isLandlord && !isTenant) {
            throw new ForbiddenException("You do not have access to this invoice");
        }
    }

    private InvoiceResponse toResponse(Invoice inv) {
        Contract contract = inv.getContract();
        Room room = contract.getRoom();
        User tenant = contract.getTenant();
        MeterReading meter = inv.getMeterReading();

        return InvoiceResponse.builder()
                .id(inv.getId())
                .billingMonth(inv.getBillingMonth())
                .status(inv.getStatus())
                .dueDate(inv.getDueDate())
                .paidAt(inv.getPaidAt())
                .contractId(contract.getId())
                .roomId(room.getId())
                .roomTitle(room.getTitle())
                .roomAddress(room.getAddress())
                .tenantId(tenant.getId())
                .tenantName(tenant.getFullName())
                .tenantPhone(tenant.getPhone())
                .landlordId(contract.getLandlord().getId())
                .landlordName(contract.getLandlord().getFullName())
                .breakdown(InvoiceResponse.Breakdown.builder()
                        .rentAmount(inv.getRentAmount())
                        .electricPrevious(meter.getElectricPrevious())
                        .electricCurrent(meter.getElectricCurrent())
                        .electricUsage(inv.getElectricUsage())
                        .electricPrice(inv.getElectricPrice())
                        .electricAmount(inv.getElectricAmount())
                        .waterPrevious(meter.getWaterPrevious())
                        .waterCurrent(meter.getWaterCurrent())
                        .waterUsage(inv.getWaterUsage())
                        .waterPrice(inv.getWaterPrice())
                        .waterAmount(inv.getWaterAmount())
                        .serviceAmount(inv.getServiceAmount())
                        .build())
                .totalAmount(inv.getTotalAmount())
                .note(inv.getNote())
                .createdAt(inv.getCreatedAt())
                .build();
    }
}
