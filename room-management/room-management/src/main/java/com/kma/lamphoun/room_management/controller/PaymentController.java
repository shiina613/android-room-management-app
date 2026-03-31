package com.kma.lamphoun.room_management.controller;

import com.kma.lamphoun.room_management.common.ApiResponse;
import com.kma.lamphoun.room_management.dto.request.CreatePaymentRequest;
import com.kma.lamphoun.room_management.dto.response.PaymentResponse;
import com.kma.lamphoun.room_management.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /api/payments
     * Landlord ghi nhận thanh toán — tự động PAID khi đủ tiền
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<PaymentResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment recorded",
                        paymentService.create(userDetails.getUsername(), request)));
    }

    /**
     * GET /api/payments/{id}
     * Landlord hoặc tenant của invoice mới xem được
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getById(id, userDetails.getUsername())));
    }

    /**
     * GET /api/payments/invoices/{invoiceId}
     * Tất cả payment của một invoice
     */
    @GetMapping("/invoices/{invoiceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getByInvoice(
            @PathVariable Long invoiceId,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "paidAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getByInvoice(invoiceId, userDetails.getUsername(), pageable)));
    }

    /**
     * GET /api/payments/tenant/me
     * Tenant xem lịch sử thanh toán của chính mình
     */
    @GetMapping("/tenant/me")
    @PreAuthorize("hasRole('ROLE_TENANT')")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getMyTenantPayments(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "paidAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getByTenant(userDetails.getUsername(), pageable)));
    }

    /**
     * GET /api/payments/my
     * Landlord xem lịch sử thanh toán của mình
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getMyLandlordPayments(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "paidAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getByLandlord(userDetails.getUsername(), pageable)));
    }
}
