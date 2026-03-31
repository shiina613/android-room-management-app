package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.common.enums.RoomCategory;
import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import com.kma.lamphoun.room_management.dto.request.RoomRequest;
import com.kma.lamphoun.room_management.dto.request.UpdateRoomStatusRequest;
import com.kma.lamphoun.room_management.dto.response.RoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {

    RoomResponse create(String ownerUsername, RoomRequest request);

    Page<RoomResponse> search(RoomStatus status, RoomCategory category, String keyword, Pageable pageable);

    RoomResponse getById(Long id);

    RoomResponse update(Long id, String ownerUsername, RoomRequest request);

    RoomResponse updateStatus(Long id, String ownerUsername, UpdateRoomStatusRequest request);

    void delete(Long id, String ownerUsername);
}
