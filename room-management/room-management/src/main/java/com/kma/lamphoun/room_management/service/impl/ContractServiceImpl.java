package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.common.enums.ContractStatus;
import com.kma.lamphoun.room_management.common.enums.Role;
import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import com.kma.lamphoun.room_management.dto.request.*;
import com.kma.lamphoun.room_management.dto.response.ContractResponse;
import com.kma.lamphoun.room_management.entity.Contract;
import com.kma.lamphoun.room_management.entity.Room;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.BadRequestException;
import com.kma.lamphoun.room_management.exception.ForbiddenException;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.mapper.ContractMapper;
import com.kma.lamphoun.room_management.repository.ContractRepository;
import com.kma.lamphoun.room_management.repository.RoomRepository;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.service.ContractService;
import com.kma.lamphoun.room_management.websocket.WebSocketEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ContractMapper contractMapper;
    private final WebSocketEventService wsEventService;

    @Override
    @Transactional
    public ContractResponse create(String landlordUsername, CreateContractRequest request) {
        // Validate dates
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        User landlord = findUserByUsername(landlordUsername);
        Room room = findRoom(request.getRoomId());
        User tenant = findTenantById(request.getTenantId());

        // Chỉ landlord sở hữu phòng mới được tạo hợp đồng
        if (!room.getOwner().getId().equals(landlord.getId())) {
            throw new ForbiddenException("You do not own this room");
        }

        // Phòng phải AVAILABLE
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new BadRequestException("Room is not available (current status: " + room.getStatus() + ")");
        }

        // Không cho 2 hợp đồng ACTIVE cùng lúc
        if (contractRepository.existsByRoomIdAndStatus(room.getId(), ContractStatus.ACTIVE)) {
            throw new BadRequestException("Room already has an active contract");
        }

        Contract contract = Contract.builder()
                .room(room)
                .tenant(tenant)
                .landlord(landlord)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .deposit(request.getDeposit())
                .monthlyRent(room.getPrice())   // snapshot giá tại thời điểm ký
                .build();

        contractRepository.save(contract);

        // Chuyển phòng sang OCCUPIED
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        ContractResponse response = contractMapper.toResponse(contract);

        // Push đến tenant + landlord
        wsEventService.pushContractCreated(tenant.getUsername(), landlord.getUsername(), response);
        // Push room status đến landlord
        wsEventService.pushRoomStatusChanged(landlord.getUsername(),
                buildRoomResponse(room, landlord));

        return response;
    }

    @Override
    public Page<ContractResponse> search(ContractStatus status, Long landlordId,
                                         Long tenantId, Long roomId, Pageable pageable) {
        return contractRepository.search(status, landlordId, tenantId, roomId, pageable)
                .map(contractMapper::toResponse);
    }

    @Override
    public ContractResponse getById(Long id) {
        return contractMapper.toResponse(findContract(id));
    }

    @Override
    @Transactional
    public ContractResponse update(Long id, String landlordUsername, UpdateContractRequest request) {
        Contract contract = findContract(id);
        checkLandlordOwnership(contract, landlordUsername);

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE contracts can be updated");
        }

        // Validate dates nếu có thay đổi
        LocalDateRange range = resolveRange(
                request.getStartDate() != null ? request.getStartDate() : contract.getStartDate(),
                request.getEndDate() != null ? request.getEndDate() : contract.getEndDate());
        if (!range.end().isAfter(range.start())) {
            throw new BadRequestException("End date must be after start date");
        }

        if (request.getStartDate() != null) contract.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)   contract.setEndDate(request.getEndDate());
        if (request.getDeposit() != null)   contract.setDeposit(request.getDeposit());

        return contractMapper.toResponse(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public ContractResponse terminate(Long id, String landlordUsername, TerminateContractRequest request) {
        Contract contract = findContract(id);
        checkLandlordOwnership(contract, landlordUsername);

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE contracts can be terminated");
        }

        contract.setStatus(ContractStatus.TERMINATED);
        contract.setTerminatedAt(request.getTerminatedAt());
        contract.setTerminationNote(request.getNote());
        contractRepository.save(contract);

        // Chuyển phòng về AVAILABLE nếu không còn hợp đồng ACTIVE nào khác
        Room room = contract.getRoom();
        if (!contractRepository.existsByRoomIdAndStatus(room.getId(), ContractStatus.ACTIVE)) {
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }

        ContractResponse response = contractMapper.toResponse(contract);

        // Push terminate đến tenant
        wsEventService.pushContractTerminated(contract.getTenant().getUsername(), response);
        // Push room status đến landlord
        wsEventService.pushRoomStatusChanged(contract.getLandlord().getUsername(),
                buildRoomResponse(room, contract.getLandlord()));

        return response;
    }

    @Override
    @Transactional
    public ContractResponse extend(Long id, String landlordUsername, ExtendContractRequest request) {
        Contract contract = findContract(id);
        checkLandlordOwnership(contract, landlordUsername);

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE contracts can be extended");
        }
        if (!request.getNewEndDate().isAfter(contract.getEndDate())) {
            throw new BadRequestException("New end date must be after current end date (" + contract.getEndDate() + ")");
        }

        contract.setEndDate(request.getNewEndDate());
        return contractMapper.toResponse(contractRepository.save(contract));
    }

    // --- helpers ---

    private Contract findContract(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with id: " + id));
    }

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private User findTenantById(Long id) {
        return userRepository.findByIdAndRole(id, Role.ROLE_TENANT)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }

    private void checkLandlordOwnership(Contract contract, String username) {
        if (!contract.getLandlord().getUsername().equals(username)) {
            throw new ForbiddenException("You do not have permission to modify this contract");
        }
    }

    private record LocalDateRange(java.time.LocalDate start, java.time.LocalDate end) {}

    private LocalDateRange resolveRange(java.time.LocalDate start, java.time.LocalDate end) {
        return new LocalDateRange(start, end);
    }

    private com.kma.lamphoun.room_management.dto.response.RoomResponse buildRoomResponse(
            Room room, User owner) {
        return com.kma.lamphoun.room_management.dto.response.RoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .address(room.getAddress())
                .status(room.getStatus())
                .category(room.getCategory())
                .price(room.getPrice())
                .ownerId(owner.getId())
                .ownerName(owner.getFullName())
                .build();
    }
}
