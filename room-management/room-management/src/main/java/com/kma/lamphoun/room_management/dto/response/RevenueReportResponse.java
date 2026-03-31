package com.kma.lamphoun.room_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevenueReportResponse {

    private int year;
    private Integer month;              // null nếu là báo cáo cả năm

    private BigDecimal totalInvoiced;   // tổng hóa đơn phát sinh
    private BigDecimal totalCollected;  // tổng đã thu (payments)
    private BigDecimal totalDebt;       // chưa thu = invoiced - collected

    private long invoiceCount;
    private long paidCount;
    private long unpaidCount;
    private long overdueCount;

    /** Breakdown từng tháng — có khi query theo năm */
    private List<MonthlyRevenue> monthly;

    @Data
    @Builder
    public static class MonthlyRevenue {
        private String month;           // "YYYY-MM"
        private BigDecimal invoiced;
        private BigDecimal collected;
        private BigDecimal debt;
        private long invoiceCount;
    }
}
