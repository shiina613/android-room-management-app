package com.kma.lamphoun.room_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ExtendContractRequest {

    @NotNull(message = "New end date is required")
    private LocalDate newEndDate;
}
