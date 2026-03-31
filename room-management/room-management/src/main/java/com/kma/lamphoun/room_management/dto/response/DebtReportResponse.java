package com.kma.lamphoun.room_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebtReportResponse {

    private BigDecimal totalDebt;
    private long debtorCount;

    private List<DebtDetail> details;

    @Data
    @Builder
    public static class DebtDetail {
        private Long invoiceId;
        private String billingMonth;
        private Long tenantId;
        private String tenantName;
        private String tenantPhone;
        private Long roomId;
        private String roomTitle;
        private BigDecimal invoiceTotal;
        private BigDecimal paid;
        private BigDecimal remaining;
        private String dueDate;
        private String status;
    }
}
