package com.kma.lamphoun.room_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeterReadingResponse {
    private Long id;

    private Long roomId;
    private String roomTitle;

    private String billingMonth;

    // Điện
    private Double electricPrevious;
    private Double electricCurrent;
    private Double electricUsage;       // current - previous
    private BigDecimal electricPrice;   // giá/kWh tại thời điểm ghi
    private BigDecimal electricAmount;  // electricUsage * electricPrice

    // Nước
    private Double waterPrevious;
    private Double waterCurrent;
    private Double waterUsage;          // current - previous
    private BigDecimal waterPrice;      // giá/m³ tại thời điểm ghi
    private BigDecimal waterAmount;     // waterUsage * waterPrice

    private String note;
    private String recordedBy;
    private LocalDateTime createdAt;
}
