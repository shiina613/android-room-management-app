package com.kma.lamphoun.room_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateInvoiceRequest {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotNull(message = "Meter reading ID is required")
    private Long meterReadingId;

    @NotBlank(message = "Billing month is required")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$",
             message = "Billing month must be in format YYYY-MM")
    private String billingMonth;

    /** Hạn thanh toán — mặc định cuối tháng nếu null */
    private LocalDate dueDate;

    private String note;
}
