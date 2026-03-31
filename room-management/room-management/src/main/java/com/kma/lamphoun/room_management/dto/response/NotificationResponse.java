package com.kma.lamphoun.room_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kma.lamphoun.room_management.common.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private NotificationType type;
    private boolean read;
    private Long referenceId;
    private LocalDateTime createdAt;
}
