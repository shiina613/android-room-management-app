package com.kma.lamphoun.room_management.controller;

import com.kma.lamphoun.room_management.common.ApiResponse;
import com.kma.lamphoun.room_management.common.enums.RoomCategory;
import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import com.kma.lamphoun.room_management.dto.request.RoomRequest;
import com.kma.lamphoun.room_management.dto.request.UpdateRoomStatusRequest;
import com.kma.lamphoun.room_management.dto.response.RoomResponse;
import com.kma.lamphoun.room_management.service.FileStorageService;
import com.kma.lamphoun.room_management.service.RoomService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final FileStorageService fileStorageService;

    /**
     * POST /api/rooms
     * Chỉ LANDLORD mới được tạo phòng
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<RoomResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RoomRequest request) {
        RoomResponse room = roomService.create(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created", room));
    }

    /**
     * GET /api/rooms?status=AVAILABLE&category=STUDIO&keyword=quận1&page=0&size=10
     * Public — ai cũng xem được
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> search(
            @RequestParam(required = false) RoomStatus status,
            @RequestParam(required = false) RoomCategory category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(roomService.search(status, category, keyword, pageable)));
    }

    /**
     * GET /api/rooms/{id}
     * Public
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getById(id)));
    }

    /**
     * PUT /api/rooms/{id}
     * Chỉ owner của phòng mới được sửa
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<RoomResponse>> update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Room updated", roomService.update(id, userDetails.getUsername(), request)));
    }

    /**
     * PATCH /api/rooms/{id}/status
     * Cập nhật trạng thái phòng — chỉ owner
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<RoomResponse>> updateStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateRoomStatusRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Status updated", roomService.updateStatus(id, userDetails.getUsername(), request)));
    }

    /**
     * DELETE /api/rooms/{id}
     * Chỉ owner của phòng mới được xóa
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        roomService.delete(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Room deleted", null));
    }

    /**
     * POST /api/rooms/{id}/image
     * Upload ảnh đại diện cho phòng — chỉ owner
     */
    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<RoomResponse>> uploadImage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileStorageService.storeRoomImage(file);
            RoomResponse room = roomService.updateImage(id, userDetails.getUsername(), imageUrl);
            return ResponseEntity.ok(ApiResponse.success("Image uploaded", room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload image: " + e.getMessage()));
        }
    }
}
