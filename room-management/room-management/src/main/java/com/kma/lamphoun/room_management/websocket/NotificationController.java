package com.kma.lamphoun.room_management.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket push helper — inject vào NotificationServiceImpl để push realtime.
 *
 * Android subscribe:
 *   - Broadcast:  /topic/notifications
 *   - Per-user:   /user/queue/notifications  (STOMP user destination)
 */
@Component
@RequiredArgsConstructor
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Push đến một user cụ thể qua STOMP user destination.
     * Android dùng: stompClient.subscribe("/user/queue/notifications", callback)
     */
    public void pushToUser(String username, NotificationMessage message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }

    /**
     * Broadcast đến tất cả client đang kết nối.
     */
    public void broadcast(NotificationMessage message) {
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }
}
