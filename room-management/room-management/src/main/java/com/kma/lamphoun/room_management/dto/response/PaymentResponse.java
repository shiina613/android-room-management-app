package com.kma.lamphoun.room_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.common.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private Long id;

    // Invoice summary
    private Long invoiceId;
    private String billingMonth;
    private BigDecimal invoiceTotal;
    private InvoiceStatus invoiceStatus;

    // Tenant info
    private Long tenantId;
    private String tenantName;

    // Room info
    private Long roomId;
    private String roomTitle;

    // Payment detail
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private LocalDate paidAt;
    private String note;
    private String recordedBy;

    /** Tổng đã thanh toán cho invoice này (bao gồm payment hiện tại) */
    private BigDecimal totalPaid;

    /** Còn lại phải trả */
    private BigDecimal remaining;

    private LocalDateTime createdAt;
}
