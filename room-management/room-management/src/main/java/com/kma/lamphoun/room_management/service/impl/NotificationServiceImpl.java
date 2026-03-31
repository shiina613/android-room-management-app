package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.common.enums.NotificationType;
import com.kma.lamphoun.room_management.dto.response.NotificationResponse;
import com.kma.lamphoun.room_management.dto.response.UnreadCountResponse;
import com.kma.lamphoun.room_management.entity.Notification;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.ForbiddenException;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.repository.NotificationRepository;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.service.NotificationService;
import com.kma.lamphoun.room_management.websocket.NotificationController;
import com.kma.lamphoun.room_management.websocket.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationController wsController;

    @Override
    @Transactional
    public NotificationResponse send(Long userId, String title, String content,
                                     NotificationType type, Long referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .type(type)
                .referenceId(referenceId)
                .build();

        notificationRepository.save(notification);

        NotificationResponse response = toResponse(notification);

        // Push realtime qua WebSocket — không throw nếu user offline
        try {
            wsController.pushToUser(user.getUsername(), toWsMessage(notification));
        } catch (Exception e) {
            log.warn("WebSocket push failed for user {}: {}", user.getUsername(), e.getMessage());
        }

        return response;
    }

    @Override
    public Page<NotificationResponse> getMyNotifications(String username, Boolean unreadOnly, Pageable pageable) {
        User user = findUser(username);
        if (Boolean.TRUE.equals(unreadOnly)) {
            return notificationRepository
                    .findByUserIdAndReadOrderByCreatedAtDesc(user.getId(), false, pageable)
                    .map(this::toResponse);
        }
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::toResponse);
    }

    @Override
    public UnreadCountResponse countUnread(String username) {
        User user = findUser(username);
        long count = notificationRepository.countByUserIdAndRead(user.getId(), false);
        return new UnreadCountResponse(count);
    }

    @Override
    @Transactional
    public void markRead(Long id, String username) {
        User user = findUser(username);
        // Dùng @Modifying query — chỉ update nếu đúng owner
        int updated = notificationRepository.markAsRead(id, user.getId());
        if (updated == 0) {
            // Kiểm tra tồn tại hay không đúng chủ
            boolean exists = notificationRepository.existsById(id);
            if (!exists) throw new ResourceNotFoundException("Notification not found: " + id);
            throw new ForbiddenException("You do not own this notification");
        }
    }

    @Override
    @Transactional
    public void markAllRead(String username) {
        User user = findUser(username);
        notificationRepository.markAllAsRead(user.getId());
    }

    // --- helpers ---

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .read(n.isRead())
                .referenceId(n.getReferenceId())
                .createdAt(n.getCreatedAt())
                .build();
    }

    private NotificationMessage toWsMessage(Notification n) {
        return NotificationMessage.builder()
                .id(n.getId())
                .type(n.getType().name())
                .title(n.getTitle())
                .content(n.getContent())
                .referenceId(n.getReferenceId())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
