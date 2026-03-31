package com.kma.lamphoun.room_management.controller;

import com.kma.lamphoun.room_management.common.ApiResponse;
import com.kma.lamphoun.room_management.dto.response.*;
import com.kma.lamphoun.room_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN')")
public class ReportController {

    private final ReportService reportService;

    /**
     * GET /api/reports/dashboard
     * Tổng hợp cho màn hình home Android — 1 request đủ dùng
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getDashboard(userDetails.getUsername())));
    }

    /**
     * GET /api/reports/revenue/monthly?year=2026&month=3
     * Doanh thu tháng cụ thể
     */
    @GetMapping("/revenue/monthly")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getMonthlyRevenue(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "#{T(java.time.YearMonth).now().year}") int year,
            @RequestParam(defaultValue = "#{T(java.time.YearMonth).now().monthValue}") int month) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getMonthlyRevenue(userDetails.getUsername(), year, month)));
    }

    /**
     * GET /api/reports/revenue/yearly?year=2026
     * Doanh thu breakdown 12 tháng trong năm
     */
    @GetMapping("/revenue/yearly")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getYearlyRevenue(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") int year) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getYearlyRevenue(userDetails.getUsername(), year)));
    }

    /**
     * GET /api/reports/debt?billingMonth=2026-03
     * Công nợ — có thể filter theo tháng hoặc lấy tất cả
     */
    @GetMapping("/debt")
    public ResponseEntity<ApiResponse<DebtReportResponse>> getDebtReport(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String billingMonth) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getDebtReport(userDetails.getUsername(), billingMonth)));
    }

    /**
     * GET /api/reports/rooms
     * Tình trạng phòng + danh sách phòng đang thuê
     */
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<RoomStatusReportResponse>> getRoomStatus(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getRoomStatusReport(userDetails.getUsername())));
    }
}
