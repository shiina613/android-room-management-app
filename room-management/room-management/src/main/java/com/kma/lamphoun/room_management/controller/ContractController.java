package com.kma.lamphoun.room_management.controller;

import com.kma.lamphoun.room_management.common.ApiResponse;
import com.kma.lamphoun.room_management.common.enums.ContractStatus;
import com.kma.lamphoun.room_management.dto.request.*;
import com.kma.lamphoun.room_management.dto.response.ContractResponse;
import com.kma.lamphoun.room_management.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    /**
     * POST /api/contracts
     * Landlord tạo hợp đồng cho phòng của mình
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<ContractResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateContractRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contract created",
                        contractService.create(userDetails.getUsername(), request)));
    }

    /**
     * GET /api/contracts/my
     * Tenant xem hợp đồng của chính mình
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_TENANT')")
    public ResponseEntity<ApiResponse<Page<ContractResponse>>> getMyContracts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) ContractStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                contractService.getByTenantUsername(userDetails.getUsername(), status, pageable)));
    }

    /**
     * GET /api/contracts?status=ACTIVE&tenantId=2&roomId=3
     * Tìm kiếm hợp đồng — LANDLORD tự động filter theo mình, ADMIN xem tất cả
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Page<ContractResponse>>> search(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long roomId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // LANDLORD chỉ xem hợp đồng của mình; ADMIN có thể truyền landlordId hoặc xem tất cả
        Long landlordId = null;
        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LANDLORD"))) {
            // Lấy landlordId từ username
            landlordId = contractService.getLandlordIdByUsername(userDetails.getUsername());
        }
        return ResponseEntity.ok(ApiResponse.success(
                contractService.search(status, landlordId, tenantId, roomId, pageable)));
    }

    /**
     * GET /api/contracts/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN', 'ROLE_TENANT')")
    public ResponseEntity<ApiResponse<ContractResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(contractService.getById(id)));
    }

    /**
     * PUT /api/contracts/{id}
     * Cập nhật ngày / tiền cọc — chỉ landlord sở hữu
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<ContractResponse>> update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateContractRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Contract updated",
                contractService.update(id, userDetails.getUsername(), request)));
    }

    /**
     * PATCH /api/contracts/{id}/terminate
     * Chấm dứt hợp đồng sớm → room về AVAILABLE
     */
    @PatchMapping("/{id}/terminate")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<ContractResponse>> terminate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TerminateContractRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Contract terminated",
                contractService.terminate(id, userDetails.getUsername(), request)));
    }

    /**
     * PATCH /api/contracts/{id}/extend
     * Gia hạn hợp đồng
     */
    @PatchMapping("/{id}/extend")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<ContractResponse>> extend(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ExtendContractRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Contract extended",
                contractService.extend(id, userDetails.getUsername(), request)));
    }
}
