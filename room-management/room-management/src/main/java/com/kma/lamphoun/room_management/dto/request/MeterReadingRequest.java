package com.kma.lamphoun.room_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MeterReadingRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    /**
     * Kỳ ghi chỉ số, định dạng YYYY-MM (VD: 2026-03)
     */
    @NotBlank(message = "Billing month is required")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$",
             message = "Billing month must be in format YYYY-MM (e.g. 2026-03)")
    private String billingMonth;

    @NotNull(message = "Electric current reading is required")
    @DecimalMin(value = "0.0", message = "Electric reading must be >= 0")
    private Double electricCurrent;

    @NotNull(message = "Water current reading is required")
    @DecimalMin(value = "0.0", message = "Water reading must be >= 0")
    private Double waterCurrent;

    private String note;
}
