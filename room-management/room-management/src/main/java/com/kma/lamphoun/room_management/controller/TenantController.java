package com.kma.lamphoun.room_management.controller;

import com.kma.lamphoun.room_management.common.ApiResponse;
import com.kma.lamphoun.room_management.dto.request.CreateTenantRequest;
import com.kma.lamphoun.room_management.dto.request.UpdateTenantRequest;
import com.kma.lamphoun.room_management.dto.response.TenantResponse;
import com.kma.lamphoun.room_management.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN')")
public class TenantController {

    private final TenantService tenantService;

    /**
     * POST /api/tenants
     * Landlord tạo tài khoản tenant mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> create(
            @Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tenant created", tenantService.create(request)));
    }

    /**
     * GET /api/tenants?page=0&size=10&sort=createdAt,desc
     * Danh sách tất cả tenant có phân trang
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TenantResponse>>> getAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(tenantService.getAll(pageable)));
    }

    /**
     * GET /api/tenants/{id}
     * Chi tiết một tenant
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(tenantService.getById(id)));
    }

    /**
     * PUT /api/tenants/{id}
     * Cập nhật thông tin tenant (fullName, email, phone, enabled)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTenantRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Tenant updated", tenantService.update(id, request)));
    }
}
