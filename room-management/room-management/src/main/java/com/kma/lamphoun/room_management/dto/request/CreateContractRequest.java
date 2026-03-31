package com.kma.lamphoun.room_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateContractRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Tenant ID is required")
    private Long tenantId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Deposit is required")
    @DecimalMin(value = "0.0", message = "Deposit must be >= 0")
    private BigDecimal deposit;
}
