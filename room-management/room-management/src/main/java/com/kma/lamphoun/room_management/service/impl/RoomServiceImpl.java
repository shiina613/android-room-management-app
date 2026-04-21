package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.common.enums.RoomCategory;
import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import com.kma.lamphoun.room_management.dto.request.RoomRequest;
import com.kma.lamphoun.room_management.dto.request.UpdateRoomStatusRequest;
import com.kma.lamphoun.room_management.dto.response.RoomResponse;
import com.kma.lamphoun.room_management.entity.Room;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.BadRequestException;
import com.kma.lamphoun.room_management.exception.ForbiddenException;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.mapper.RoomMapper;
import com.kma.lamphoun.room_management.repository.ContractRepository;
import com.kma.lamphoun.room_management.repository.RoomRepository;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.service.RoomService;
import com.kma.lamphoun.room_management.websocket.WebSocketEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final RoomMapper roomMapper;
    private final WebSocketEventService wsEventService;

    @Override
    @Transactional
    public RoomResponse create(String ownerUsername, RoomRequest request) {
        User owner = findUser(ownerUsername);
        Room room = Room.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .price(request.getPrice())
                .area(request.getArea())
                .elecPrice(request.getElecPrice())
                .waterPrice(request.getWaterPrice())
                .servicePrice(request.getServicePrice())
                .category(request.getCategory())
                .owner(owner)
                .build();
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    public Page<RoomResponse> search(RoomStatus status, RoomCategory category, String keyword, Pageable pageable) {
        return roomRepository.search(status, category, keyword, pageable)
                .map(roomMapper::toResponse);
    }

    @Override
    public RoomResponse getById(Long id) {
        return roomMapper.toResponse(findRoom(id));
    }

    @Override
    @Transactional
    public RoomResponse update(Long id, String ownerUsername, RoomRequest request) {
        Room room = findRoom(id);
        checkOwnership(room, ownerUsername);

        room.setTitle(request.getTitle());
        room.setDescription(request.getDescription());
        room.setAddress(request.getAddress());
        room.setPrice(request.getPrice());
        room.setArea(request.getArea());
        room.setElecPrice(request.getElecPrice());
        room.setWaterPrice(request.getWaterPrice());
        room.setServicePrice(request.getServicePrice());
        room.setCategory(request.getCategory());

        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    public RoomResponse updateStatus(Long id, String ownerUsername, UpdateRoomStatusRequest request) {
        Room room = findRoom(id);
        checkOwnership(room, ownerUsername);
        room.setStatus(request.getStatus());
        RoomResponse response = roomMapper.toResponse(roomRepository.save(room));

        // Push trạng thái mới đến landlord + broadcast
        wsEventService.pushRoomStatusChanged(ownerUsername, response);

        return response;
    }

    @Override
    @Transactional
    public RoomResponse updateImage(Long id, String ownerUsername, String imageUrl) {
        Room room = findRoom(id);
        checkOwnership(room, ownerUsername);
        room.setImageUrl(imageUrl);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    public void delete(Long id, String ownerUsername) {
        Room room = findRoom(id);
        checkOwnership(room, ownerUsername);

        // Không cho xóa phòng đang có hợp đồng ACTIVE
        if (contractRepository.existsByRoomIdAndStatus(room.getId(),
                com.kma.lamphoun.room_management.common.enums.ContractStatus.ACTIVE)) {
            throw new com.kma.lamphoun.room_management.exception.ConflictException(
                    "Cannot delete room with an active contract");
        }

        roomRepository.delete(room);
    }

    // --- helpers ---

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private void checkOwnership(Room room, String username) {
        if (!room.getOwner().getUsername().equals(username)) {
            throw new ForbiddenException("You do not have permission to modify this room");
        }
    }
}
