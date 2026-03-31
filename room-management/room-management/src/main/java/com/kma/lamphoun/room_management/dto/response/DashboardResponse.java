package com.kma.lamphoun.room_management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Tổng hợp cho màn hình Dashboard Android — 1 request lấy đủ dữ liệu.
 */
@Data
@Builder
public class DashboardResponse {

    // Phòng
    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private long maintenanceRooms;
    private double occupancyRate;

    // Tháng hiện tại
    private String currentMonth;
    private BigDecimal revenueThisMonth;    // tổng invoice phát sinh
    private BigDecimal collectedThisMonth;  // đã thu
    private BigDecimal debtThisMonth;       // chưa thu

    // Hợp đồng
    private long activeContracts;
    private long expiringIn30Days;          // sắp hết hạn

    // Tenant
    private long totalTenants;

    // Invoice
    private long unpaidInvoices;
    private long overdueInvoices;
}
