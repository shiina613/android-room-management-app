package com.kma.lamphoun.room_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kma.lamphoun.room_management.common.enums.ContractStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractResponse {
    private Long id;

    // Room info
    private Long roomId;
    private String roomTitle;
    private String roomAddress;

    // Tenant info
    private Long tenantId;
    private String tenantName;
    private String tenantPhone;

    // Landlord info
    private Long landlordId;
    private String landlordName;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal deposit;
    private BigDecimal monthlyRent;
    private ContractStatus status;
    private String terminationNote;
    private LocalDate terminatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
