package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.common.enums.ContractStatus;
import com.kma.lamphoun.room_management.dto.request.*;
import com.kma.lamphoun.room_management.dto.response.ContractResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContractService {

    ContractResponse create(String landlordUsername, CreateContractRequest request);

    Page<ContractResponse> search(ContractStatus status, Long landlordId, Long tenantId, Long roomId, Pageable pageable);

    ContractResponse getById(Long id);

    ContractResponse update(Long id, String landlordUsername, UpdateContractRequest request);

    ContractResponse terminate(Long id, String landlordUsername, TerminateContractRequest request);

    ContractResponse extend(Long id, String landlordUsername, ExtendContractRequest request);

    /** Lấy landlordId từ username — dùng trong controller để auto-filter */
    Long getLandlordIdByUsername(String username);

    /** Tenant xem hợp đồng của chính mình */
    Page<ContractResponse> getByTenantUsername(String tenantUsername, ContractStatus status, Pageable pageable);
}
