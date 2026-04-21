package com.kma.lamphoun.room_management.mapper;

import com.kma.lamphoun.room_management.dto.response.RoomResponse;
import com.kma.lamphoun.room_management.entity.Room;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public RoomResponse toResponse(Room room) {
        String imageUrl = null;
        if (room.getImageUrl() != null && !room.getImageUrl().isBlank()) {
            String path = room.getImageUrl();
            // Nếu đã là URL đầy đủ thì giữ nguyên, nếu là path tương đối thì ghép base URL
            imageUrl = path.startsWith("http") ? path : baseUrl + path;
        }

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
                .imageUrl(imageUrl)
                .ownerId(room.getOwner().getId())
                .ownerName(room.getOwner().getFullName())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
