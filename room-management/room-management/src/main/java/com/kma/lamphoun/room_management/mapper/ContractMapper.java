package com.kma.lamphoun.room_management.mapper;

import com.kma.lamphoun.room_management.dto.response.ContractResponse;
import com.kma.lamphoun.room_management.entity.Contract;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    public ContractResponse toResponse(Contract c) {
        return ContractResponse.builder()
                .id(c.getId())
                .roomId(c.getRoom().getId())
                .roomTitle(c.getRoom().getTitle())
                .roomAddress(c.getRoom().getAddress())
                .tenantId(c.getTenant().getId())
                .tenantName(c.getTenant().getFullName())
                .tenantPhone(c.getTenant().getPhone())
                .landlordId(c.getLandlord().getId())
                .landlordName(c.getLandlord().getFullName())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .deposit(c.getDeposit())
                .monthlyRent(c.getMonthlyRent())
                .status(c.getStatus())
                .terminationNote(c.getTerminationNote())
                .terminatedAt(c.getTerminatedAt())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
