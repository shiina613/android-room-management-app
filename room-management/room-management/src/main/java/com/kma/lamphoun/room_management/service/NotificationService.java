package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.common.enums.NotificationType;
import com.kma.lamphoun.room_management.dto.response.NotificationResponse;
import com.kma.lamphoun.room_management.dto.response.UnreadCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    /**
     * Tạo và push realtime đến user — dùng nội bộ từ các module khác
     */
    NotificationResponse send(Long userId, String title, String content,
                              NotificationType type, Long referenceId);

    /** Lấy tất cả notification của user hiện tại */
    Page<NotificationResponse> getMyNotifications(String username, Boolean unreadOnly, Pageable pageable);

    /** Số thông báo chưa đọc */
    UnreadCountResponse countUnread(String username);

    /** Đánh dấu một thông báo đã đọc */
    void markRead(Long id, String username);

    /** Đánh dấu tất cả đã đọc */
    void markAllRead(String username);
}
