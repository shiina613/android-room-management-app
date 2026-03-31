package com.kma.lamphoun.room_management.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateContractRequest {

    private LocalDate startDate;
    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "Deposit must be >= 0")
    private BigDecimal deposit;
}
