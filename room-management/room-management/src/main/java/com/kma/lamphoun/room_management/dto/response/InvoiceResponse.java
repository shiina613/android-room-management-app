package com.kma.lamphoun.room_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceResponse {

    private Long id;
    private String billingMonth;
    private InvoiceStatus status;
    private LocalDate dueDate;
    private LocalDate paidAt;

    // --- Contract info ---
    private Long contractId;
    private Long roomId;
    private String roomTitle;
    private String roomAddress;
    private Long tenantId;
    private String tenantName;
    private String tenantPhone;
    private Long landlordId;
    private String landlordName;

    // --- Breakdown chi tiết từng khoản ---
    private Breakdown breakdown;

    private BigDecimal totalAmount;
    private String note;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Breakdown {
        // Tiền thuê phòng
        private BigDecimal rentAmount;

        // Điện
        private Double electricPrevious;
        private Double electricCurrent;
        private Double electricUsage;
        private BigDecimal electricPrice;   // đơn giá/kWh
        private BigDecimal electricAmount;  // = usage * price

        // Nước
        private Double waterPrevious;
        private Double waterCurrent;
        private Double waterUsage;
        private BigDecimal waterPrice;      // đơn giá/m³
        private BigDecimal waterAmount;     // = usage * price

        // Phí dịch vụ
        private BigDecimal serviceAmount;
    }
}
