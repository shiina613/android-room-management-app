package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.dto.request.CreatePaymentRequest;
import com.kma.lamphoun.room_management.dto.response.PaymentResponse;
import com.kma.lamphoun.room_management.entity.Contract;
import com.kma.lamphoun.room_management.entity.Invoice;
import com.kma.lamphoun.room_management.entity.Payment;
import com.kma.lamphoun.room_management.entity.Room;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.BadRequestException;
import com.kma.lamphoun.room_management.exception.ForbiddenException;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.repository.InvoiceRepository;
import com.kma.lamphoun.room_management.repository.PaymentRepository;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentResponse create(String landlordUsername, CreatePaymentRequest request) {
        User landlord = findUser(landlordUsername);
        Invoice invoice = findInvoice(request.getInvoiceId());

        // Chỉ landlord của invoice mới ghi nhận được
        if (!invoice.getContract().getLandlord().getId().equals(landlord.getId())) {
            throw new ForbiddenException("You do not own this invoice");
        }

        // Invoice đã PAID thì không nhận thêm
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Invoice is already fully paid");
        }

        // Số tiền không được vượt quá số còn lại
        BigDecimal alreadyPaid = paymentRepository.sumAmountByInvoiceId(invoice.getId());
        BigDecimal remaining = invoice.getTotalAmount().subtract(alreadyPaid);

        if (request.getAmount().compareTo(remaining) > 0) {
            throw new BadRequestException(
                    "Payment amount (" + request.getAmount()
                    + ") exceeds remaining balance (" + remaining + ")");
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paidAt(request.getPaidAt())
                .note(request.getNote())
                .recordedBy(landlord)
                .build();

        paymentRepository.save(payment);

        // Tính lại tổng đã trả sau khi lưu payment mới
        BigDecimal newTotalPaid = alreadyPaid.add(request.getAmount());
        BigDecimal newRemaining = invoice.getTotalAmount().subtract(newTotalPaid);

        // Tự động chuyển PAID khi đủ tiền
        if (newRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(request.getPaidAt());
            invoiceRepository.save(invoice);
        }

        return toResponse(payment, invoice, newTotalPaid, newRemaining);
    }

    @Override
    public PaymentResponse getById(Long id, String username) {
        Payment payment = findPayment(id);
        checkViewPermission(payment, username);
        BigDecimal totalPaid = paymentRepository.sumAmountByInvoiceId(payment.getInvoice().getId());
        BigDecimal remaining = payment.getInvoice().getTotalAmount().subtract(totalPaid);
        return toResponse(payment, payment.getInvoice(), totalPaid, remaining);
    }

    @Override
    public Page<PaymentResponse> getByInvoice(Long invoiceId, String username, Pageable pageable) {
        Invoice invoice = findInvoice(invoiceId);
        User user = findUser(username);

        boolean isLandlord = invoice.getContract().getLandlord().getId().equals(user.getId());
        boolean isTenant   = invoice.getContract().getTenant().getId().equals(user.getId());
        if (!isLandlord && !isTenant) {
            throw new ForbiddenException("You do not have access to this invoice's payments");
        }

        BigDecimal totalPaid = paymentRepository.sumAmountByInvoiceId(invoiceId);
        BigDecimal remaining = invoice.getTotalAmount().subtract(totalPaid);

        return paymentRepository.findByInvoiceIdOrderByPaidAtDesc(invoiceId, pageable)
                .map(p -> toResponse(p, invoice, totalPaid, remaining));
    }

    @Override
    public Page<PaymentResponse> getByTenant(String tenantUsername, Pageable pageable) {
        User tenant = findUser(tenantUsername);
        return paymentRepository.findByTenantId(tenant.getId(), pageable)
                .map(p -> {
                    BigDecimal totalPaid = paymentRepository.sumAmountByInvoiceId(p.getInvoice().getId());
                    BigDecimal remaining = p.getInvoice().getTotalAmount().subtract(totalPaid);
                    return toResponse(p, p.getInvoice(), totalPaid, remaining);
                });
    }

    @Override
    public Page<PaymentResponse> getByLandlord(String landlordUsername, Pageable pageable) {
        User landlord = findUser(landlordUsername);
        return paymentRepository.findByLandlordId(landlord.getId(), pageable)
                .map(p -> {
                    BigDecimal totalPaid = paymentRepository.sumAmountByInvoiceId(p.getInvoice().getId());
                    BigDecimal remaining = p.getInvoice().getTotalAmount().subtract(totalPaid);
                    return toResponse(p, p.getInvoice(), totalPaid, remaining);
                });
    }

    // --- helpers ---

    private Payment findPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    private Invoice findInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private void checkViewPermission(Payment payment, String username) {
        Contract contract = payment.getInvoice().getContract();
        boolean isLandlord = contract.getLandlord().getUsername().equals(username);
        boolean isTenant   = contract.getTenant().getUsername().equals(username);
        if (!isLandlord && !isTenant) {
            throw new ForbiddenException("You do not have access to this payment");
        }
    }

    private PaymentResponse toResponse(Payment p, Invoice invoice,
                                        BigDecimal totalPaid, BigDecimal remaining) {
        Contract contract = invoice.getContract();
        Room room = contract.getRoom();
        User tenant = contract.getTenant();

        return PaymentResponse.builder()
                .id(p.getId())
                .invoiceId(invoice.getId())
                .billingMonth(invoice.getBillingMonth())
                .invoiceTotal(invoice.getTotalAmount())
                .invoiceStatus(invoice.getStatus())
                .tenantId(tenant.getId())
                .tenantName(tenant.getFullName())
                .roomId(room.getId())
                .roomTitle(room.getTitle())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .paidAt(p.getPaidAt())
                .note(p.getNote())
                .recordedBy(p.getRecordedBy().getUsername())
                .totalPaid(totalPaid)
                .remaining(remaining.max(BigDecimal.ZERO))
                .createdAt(p.getCreatedAt())
                .build();
    }
}
