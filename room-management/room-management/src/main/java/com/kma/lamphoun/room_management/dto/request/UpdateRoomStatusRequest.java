package com.kma.lamphoun.room_management.dto.request;

import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoomStatusRequest {

    @NotNull(message = "Status is required")
    private RoomStatus status;
}
