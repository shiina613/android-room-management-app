package com.kma.lamphoun.room_management.mapper;

import com.kma.lamphoun.room_management.dto.response.RoomResponse;
import com.kma.lamphoun.room_management.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .description(room.getDescription())
                .address(room.getAddress())
                .price(room.getPrice())
                .area(room.getArea())
                .elecPrice(room.getElecPrice())
                .waterPrice(room.getWaterPrice())
                .servicePrice(room.getServicePrice())
                .status(room.getStatus())
                .category(room.getCategory())
                .ownerId(room.getOwner().getId())
                .ownerName(room.getOwner().getFullName())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
