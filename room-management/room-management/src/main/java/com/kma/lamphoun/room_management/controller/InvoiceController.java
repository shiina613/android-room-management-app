package com.kma.lamphoun.room_management.controller;

import com.kma.lamphoun.room_management.common.ApiResponse;
import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.dto.request.CreateInvoiceRequest;
import com.kma.lamphoun.room_management.dto.request.MarkPaidRequest;
import com.kma.lamphoun.room_management.dto.response.InvoiceResponse;
import com.kma.lamphoun.room_management.service.InvoiceService;
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
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    /**
     * POST /api/invoices
     * Landlord tạo invoice từ contract + meter reading
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoice created",
                        invoiceService.create(userDetails.getUsername(), request)));
    }

    /**
     * GET /api/invoices/{id}
     * Landlord hoặc tenant của invoice mới xem được
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.getById(id, userDetails.getUsername())));
    }

    /**
     * GET /api/invoices/my?status=UNPAID
     * Landlord xem invoice của mình
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getMyInvoices(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) Long contractId,
            @PageableDefault(size = 10, sort = "billingMonth", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.getByLandlord(userDetails.getUsername(), status, contractId, pageable)));
    }

    /**
     * GET /api/invoices/tenant/me?status=UNPAID
     * Tenant chỉ xem invoice của chính mình
     */
    @GetMapping("/tenant/me")
    @PreAuthorize("hasRole('ROLE_TENANT')")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getMyTenantInvoices(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) InvoiceStatus status,
            @PageableDefault(size = 10, sort = "billingMonth", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.getByTenant(userDetails.getUsername(), status, pageable)));
    }

    /**
     * GET /api/invoices/contracts/{contractId}
     * Xem tất cả invoice theo contract — landlord hoặc tenant của contract
     */
    @GetMapping("/contracts/{contractId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getByContract(
            @PathVariable Long contractId,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 12, sort = "billingMonth", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.getByContract(contractId, userDetails.getUsername(), pageable)));
    }

    /**
     * PATCH /api/invoices/{id}/paid
     * Landlord đánh dấu đã thanh toán
     */
    @PatchMapping("/{id}/paid")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> markPaid(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MarkPaidRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Invoice marked as paid",
                invoiceService.markPaid(id, userDetails.getUsername(), request)));
    }
}
