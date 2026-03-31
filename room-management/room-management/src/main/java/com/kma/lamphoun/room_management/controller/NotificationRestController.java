package com.kma.lamphoun.room_management.controller;

import com.kma.lamphoun.room_management.common.ApiResponse;
import com.kma.lamphoun.room_management.dto.response.NotificationResponse;
import com.kma.lamphoun.room_management.dto.response.UnreadCountResponse;
import com.kma.lamphoun.room_management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class NotificationRestController {

    private final NotificationService notificationService;

    /**
     * GET /api/notifications?unreadOnly=true
     * Lấy danh sách thông báo của user hiện tại
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Boolean unreadOnly,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getMyNotifications(userDetails.getUsername(), unreadOnly, pageable)));
    }

    /**
     * GET /api/notifications/unread-count
     * Số thông báo chưa đọc — dùng để hiển thị badge trên Android
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> countUnread(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.countUnread(userDetails.getUsername())));
    }

    /**
     * PATCH /api/notifications/{id}/read
     * Đánh dấu một thông báo đã đọc
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markRead(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Marked as read", null));
    }

    /**
     * PATCH /api/notifications/read-all
     * Đánh dấu tất cả đã đọc
     */
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllRead(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
