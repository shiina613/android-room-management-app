package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.dto.request.CreateTenantRequest;
import com.kma.lamphoun.room_management.dto.request.UpdateTenantRequest;
import com.kma.lamphoun.room_management.dto.response.TenantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TenantService {

    TenantResponse create(CreateTenantRequest request);

    Page<TenantResponse> getAll(Pageable pageable);

    TenantResponse getById(Long id);

    TenantResponse update(Long id, UpdateTenantRequest request);
}
