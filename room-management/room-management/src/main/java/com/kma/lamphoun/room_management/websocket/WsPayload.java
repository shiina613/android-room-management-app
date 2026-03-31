package com.kma.lamphoun.room_management.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Envelope chung cho mọi WebSocket message.
 * Android parse field "event" để biết xử lý gì, "data" là payload cụ thể.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WsPayload {
    private String event;           // WsEvent constant
    private Object data;            // DTO tương ứng
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
