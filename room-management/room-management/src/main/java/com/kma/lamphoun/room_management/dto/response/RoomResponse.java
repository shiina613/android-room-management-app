package com.kma.lamphoun.room_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kma.lamphoun.room_management.common.enums.RoomCategory;
import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomResponse {
    private Long id;
    private String title;
    private String description;
    private String address;
    private BigDecimal price;
    private Double area;
    private BigDecimal elecPrice;
    private BigDecimal waterPrice;
    private BigDecimal servicePrice;
    private RoomStatus status;
    private RoomCategory category;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
