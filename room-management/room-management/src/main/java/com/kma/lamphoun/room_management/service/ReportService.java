package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.dto.response.DashboardResponse;
import com.kma.lamphoun.room_management.dto.response.DebtReportResponse;
import com.kma.lamphoun.room_management.dto.response.RevenueReportResponse;
import com.kma.lamphoun.room_management.dto.response.RoomStatusReportResponse;

public interface ReportService {

    /** Dashboard tổng hợp — 1 call cho Android home screen */
    DashboardResponse getDashboard(String username);

    /** Doanh thu theo tháng cụ thể */
    RevenueReportResponse getMonthlyRevenue(String username, int year, int month);

    /** Doanh thu breakdown cả năm (12 tháng) */
    RevenueReportResponse getYearlyRevenue(String username, int year);

    /** Công nợ — tất cả invoice chưa thanh toán */
    DebtReportResponse getDebtReport(String username, String billingMonth);

    /** Tình trạng phòng */
    RoomStatusReportResponse getRoomStatusReport(String username);
}
