package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.common.enums.Role;
import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import com.kma.lamphoun.room_management.dto.response.*;
import com.kma.lamphoun.room_management.entity.Invoice;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.repository.ReportRepository;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardResponse getDashboard(String username) {
        Long landlordId = resolveLandlordId(username);
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        long total       = reportRepository.countRooms(landlordId);
        long available   = reportRepository.countRoomsByStatus(RoomStatus.AVAILABLE, landlordId);
        long occupied    = reportRepository.countRoomsByStatus(RoomStatus.OCCUPIED, landlordId);
        long maintenance = reportRepository.countRoomsByStatus(RoomStatus.MAINTENANCE, landlordId);
        double occupancy = total > 0
                ? BigDecimal.valueOf(occupied * 100.0 / total).setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        BigDecimal invoiced  = reportRepository.sumInvoicedByMonth(currentMonth, landlordId);
        BigDecimal collected = reportRepository.sumCollectedByMonth(currentMonth, landlordId);
        BigDecimal debt      = invoiced.subtract(collected).max(BigDecimal.ZERO);

        long activeContracts  = reportRepository.countActiveContracts(landlordId);
        long expiringIn30Days = reportRepository.countExpiringContracts(
                LocalDate.now(), LocalDate.now().plusDays(30), landlordId);

        long totalTenants  = reportRepository.countByRole(Role.ROLE_TENANT);
        long unpaid        = reportRepository.countInvoiceByMonthAndStatus(currentMonth, landlordId, InvoiceStatus.UNPAID);
        long overdue       = reportRepository.countInvoiceByMonthAndStatus(currentMonth, landlordId, InvoiceStatus.OVERDUE);

        return DashboardResponse.builder()
                .totalRooms(total)
                .availableRooms(available)
                .occupiedRooms(occupied)
                .maintenanceRooms(maintenance)
                .occupancyRate(occupancy)
                .currentMonth(currentMonth)
                .revenueThisMonth(invoiced)
                .collectedThisMonth(collected)
                .debtThisMonth(debt)
                .activeContracts(activeContracts)
                .expiringIn30Days(expiringIn30Days)
                .totalTenants(totalTenants)
                .unpaidInvoices(unpaid)
                .overdueInvoices(overdue)
                .build();
    }

    @Override
    public RevenueReportResponse getMonthlyRevenue(String username, int year, int month) {
        Long landlordId = resolveLandlordId(username);
        String monthStr = String.format("%d-%02d", year, month);

        BigDecimal invoiced  = reportRepository.sumInvoicedByMonth(monthStr, landlordId);
        BigDecimal collected = reportRepository.sumCollectedByMonth(monthStr, landlordId);
        BigDecimal debt      = invoiced.subtract(collected).max(BigDecimal.ZERO);

        long total   = reportRepository.countInvoiceByMonthAndStatus(monthStr, landlordId, null);
        long paid    = reportRepository.countInvoiceByMonthAndStatus(monthStr, landlordId, InvoiceStatus.PAID);
        long unpaid  = reportRepository.countInvoiceByMonthAndStatus(monthStr, landlordId, InvoiceStatus.UNPAID);
        long overdue = reportRepository.countInvoiceByMonthAndStatus(monthStr, landlordId, InvoiceStatus.OVERDUE);

        return RevenueReportResponse.builder()
                .year(year).month(month)
                .totalInvoiced(invoiced)
                .totalCollected(collected)
                .totalDebt(debt)
                .invoiceCount(total)
                .paidCount(paid)
                .unpaidCount(unpaid)
                .overdueCount(overdue)
                .build();
    }

    @Override
    public RevenueReportResponse getYearlyRevenue(String username, int year) {
        Long landlordId = resolveLandlordId(username);
        String yearStr = String.valueOf(year);

        // Tổng invoice theo tháng
        Map<String, BigDecimal> invoicedMap = new HashMap<>();
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] row : reportRepository.monthlyBreakdown(yearStr, landlordId)) {
            invoicedMap.put((String) row[0], (BigDecimal) row[1]);
            countMap.put((String) row[0], (Long) row[2]);
        }

        // Tổng collected theo tháng
        Map<String, BigDecimal> collectedMap = new HashMap<>();
        for (Object[] row : reportRepository.monthlyCollected(yearStr, landlordId)) {
            collectedMap.put((String) row[0], (BigDecimal) row[1]);
        }

        // Build 12 tháng (kể cả tháng chưa có dữ liệu)
        List<RevenueReportResponse.MonthlyRevenue> monthly = new ArrayList<>();
        BigDecimal totalInvoiced = BigDecimal.ZERO;
        BigDecimal totalCollected = BigDecimal.ZERO;

        for (int m = 1; m <= 12; m++) {
            String key = String.format("%d-%02d", year, m);
            BigDecimal inv  = invoicedMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal col  = collectedMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal debt = inv.subtract(col).max(BigDecimal.ZERO);
            long count      = countMap.getOrDefault(key, 0L);

            monthly.add(RevenueReportResponse.MonthlyRevenue.builder()
                    .month(key).invoiced(inv).collected(col).debt(debt).invoiceCount(count)
                    .build());

            totalInvoiced  = totalInvoiced.add(inv);
            totalCollected = totalCollected.add(col);
        }

        return RevenueReportResponse.builder()
                .year(year)
                .totalInvoiced(totalInvoiced)
                .totalCollected(totalCollected)
                .totalDebt(totalInvoiced.subtract(totalCollected).max(BigDecimal.ZERO))
                .monthly(monthly)
                .build();
    }

    @Override
    public DebtReportResponse getDebtReport(String username, String billingMonth) {
        Long landlordId = resolveLandlordId(username);

        List<Invoice> unpaidInvoices = reportRepository.findUnpaidInvoices(landlordId, billingMonth);
        if (unpaidInvoices.isEmpty()) {
            return DebtReportResponse.builder()
                    .totalDebt(BigDecimal.ZERO).debtorCount(0).details(List.of()).build();
        }

        // Lấy tổng đã trả cho từng invoice
        List<Long> ids = unpaidInvoices.stream().map(Invoice::getId).toList();
        Map<Long, BigDecimal> paidMap = new HashMap<>();
        for (Object[] row : reportRepository.sumPaidByInvoiceIds(ids)) {
            paidMap.put((Long) row[0], (BigDecimal) row[1]);
        }

        BigDecimal totalDebt = BigDecimal.ZERO;
        List<DebtReportResponse.DebtDetail> details = new ArrayList<>();

        for (Invoice inv : unpaidInvoices) {
            BigDecimal paid      = paidMap.getOrDefault(inv.getId(), BigDecimal.ZERO);
            BigDecimal remaining = inv.getTotalAmount().subtract(paid).max(BigDecimal.ZERO);
            totalDebt = totalDebt.add(remaining);

            details.add(DebtReportResponse.DebtDetail.builder()
                    .invoiceId(inv.getId())
                    .billingMonth(inv.getBillingMonth())
                    .tenantId(inv.getContract().getTenant().getId())
                    .tenantName(inv.getContract().getTenant().getFullName())
                    .tenantPhone(inv.getContract().getTenant().getPhone())
                    .roomId(inv.getContract().getRoom().getId())
                    .roomTitle(inv.getContract().getRoom().getTitle())
                    .invoiceTotal(inv.getTotalAmount())
                    .paid(paid)
                    .remaining(remaining)
                    .dueDate(inv.getDueDate().toString())
                    .status(inv.getStatus().name())
                    .build());
        }

        long debtorCount = details.stream()
                .map(DebtReportResponse.DebtDetail::getTenantId)
                .distinct().count();

        return DebtReportResponse.builder()
                .totalDebt(totalDebt)
                .debtorCount(debtorCount)
                .details(details)
                .build();
    }

    @Override
    public RoomStatusReportResponse getRoomStatusReport(String username) {
        Long landlordId = resolveLandlordId(username);

        long total       = reportRepository.countRooms(landlordId);
        long available   = reportRepository.countRoomsByStatus(RoomStatus.AVAILABLE, landlordId);
        long occupied    = reportRepository.countRoomsByStatus(RoomStatus.OCCUPIED, landlordId);
        long maintenance = reportRepository.countRoomsByStatus(RoomStatus.MAINTENANCE, landlordId);
        double occupancy = total > 0
                ? BigDecimal.valueOf(occupied * 100.0 / total).setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        List<RoomStatusReportResponse.OccupiedRoomDetail> occupiedList = new ArrayList<>();
        for (Object[] row : reportRepository.findOccupiedRoomDetails(landlordId)) {
            occupiedList.add(RoomStatusReportResponse.OccupiedRoomDetail.builder()
                    .roomId((Long) row[0])
                    .roomTitle((String) row[1])
                    .address((String) row[2])
                    .tenantName((String) row[3])
                    .tenantPhone((String) row[4])
                    .contractStart(row[5].toString())
                    .contractEnd(row[6].toString())
                    .build());
        }

        return RoomStatusReportResponse.builder()
                .totalRooms(total)
                .available(available)
                .occupied(occupied)
                .maintenance(maintenance)
                .occupancyRate(occupancy)
                .occupiedRooms(occupiedList)
                .build();
    }

    // --- helper ---

    /**
     * ADMIN truyền null → query toàn hệ thống.
     * LANDLORD → chỉ query data của mình.
     */
    private Long resolveLandlordId(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return switch (user.getRole()) {
            case ROLE_ADMIN -> null;          // null = không filter theo landlord
            case ROLE_LANDLORD -> user.getId();
            default -> throw new com.kma.lamphoun.room_management.exception.ForbiddenException(
                    "Access denied for role: " + user.getRole());
        };
    }
}
