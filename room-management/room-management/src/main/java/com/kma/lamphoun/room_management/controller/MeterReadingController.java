package com.kma.lamphoun.room_management.controller;

import com.kma.lamphoun.room_management.common.ApiResponse;
import com.kma.lamphoun.room_management.dto.request.MeterReadingRequest;
import com.kma.lamphoun.room_management.dto.response.MeterReadingResponse;
import com.kma.lamphoun.room_management.service.MeterReadingService;
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
@RequestMapping("/api/meter-readings")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    /**
     * POST /api/meter-readings
     * Ghi chỉ số điện nước — LANDLORD hoặc ADMIN
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MeterReadingResponse>> record(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MeterReadingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Meter reading recorded",
                        meterReadingService.record(userDetails.getUsername(), request)));
    }

    /**
     * GET /api/meter-readings/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN', 'ROLE_TENANT')")
    public ResponseEntity<ApiResponse<MeterReadingResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(meterReadingService.getById(id)));
    }

    /**
     * GET /api/meter-readings/rooms/{roomId}
     * Lịch sử chỉ số theo phòng (phân trang)
     */
    @GetMapping("/rooms/{roomId}")
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN', 'ROLE_TENANT')")
    public ResponseEntity<ApiResponse<Page<MeterReadingResponse>>> getHistoryByRoom(
            @PathVariable Long roomId,
            @PageableDefault(size = 12, sort = "billingMonth", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                meterReadingService.getHistoryByRoom(roomId, pageable)));
    }

    /**
     * GET /api/meter-readings/rooms/{roomId}/year/{year}
     * Lịch sử theo phòng + năm (12 kỳ trong năm)
     */
    @GetMapping("/rooms/{roomId}/year/{year}")
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN', 'ROLE_TENANT')")
    public ResponseEntity<ApiResponse<Page<MeterReadingResponse>>> getHistoryByRoomAndYear(
            @PathVariable Long roomId,
            @PathVariable int year,
            @PageableDefault(size = 12, sort = "billingMonth", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                meterReadingService.getHistoryByRoomAndYear(roomId, year, pageable)));
    }

    /**
     * GET /api/meter-readings/rooms/{roomId}/month/{billingMonth}
     * Lấy chỉ số kỳ cụ thể — VD: /rooms/1/month/2026-03
     */
    @GetMapping("/rooms/{roomId}/month/{billingMonth}")
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN', 'ROLE_TENANT')")
    public ResponseEntity<ApiResponse<MeterReadingResponse>> getByRoomAndMonth(
            @PathVariable Long roomId,
            @PathVariable String billingMonth) {
        return ResponseEntity.ok(ApiResponse.success(
                meterReadingService.getByRoomAndMonth(roomId, billingMonth)));
    }
}
