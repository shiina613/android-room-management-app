package com.kma.lamphoun.room_management.websocket;

import com.kma.lamphoun.room_management.common.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private Long id;
    private String type;           // NotificationType as string
    private String title;
    private String content;
    private Long referenceId;
    private LocalDateTime createdAt;
}
