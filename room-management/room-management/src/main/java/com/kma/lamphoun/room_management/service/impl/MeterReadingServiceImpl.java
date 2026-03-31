package com.kma.lamphoun.room_management.service.impl;

import com.kma.lamphoun.room_management.dto.request.MeterReadingRequest;
import com.kma.lamphoun.room_management.dto.response.MeterReadingResponse;
import com.kma.lamphoun.room_management.entity.MeterReading;
import com.kma.lamphoun.room_management.entity.Room;
import com.kma.lamphoun.room_management.entity.User;
import com.kma.lamphoun.room_management.exception.BadRequestException;
import com.kma.lamphoun.room_management.exception.ResourceNotFoundException;
import com.kma.lamphoun.room_management.repository.MeterReadingRepository;
import com.kma.lamphoun.room_management.repository.RoomRepository;
import com.kma.lamphoun.room_management.repository.UserRepository;
import com.kma.lamphoun.room_management.service.MeterReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MeterReadingResponse record(String recorderUsername, MeterReadingRequest request) {
        Room room = findRoom(request.getRoomId());
        User recorder = findUser(recorderUsername);

        // Validate format billingMonth
        YearMonth billingMonth = parseBillingMonth(request.getBillingMonth());

        // Không cho ghi kỳ tương lai
        if (billingMonth.isAfter(YearMonth.now())) {
            throw new BadRequestException("Cannot record meter reading for a future month: " + request.getBillingMonth());
        }

        // Không cho ghi trùng kỳ
        if (meterReadingRepository.existsByRoomIdAndBillingMonth(room.getId(), request.getBillingMonth())) {
            throw new BadRequestException("Meter reading for room " + room.getId()
                    + " in month " + request.getBillingMonth() + " already exists");
        }

        // Lấy chỉ số kỳ trước để làm previous và validate
        Optional<MeterReading> latestOpt = meterReadingRepository.findLatestByRoomId(room.getId());

        double electricPrevious = 0.0;
        double waterPrevious = 0.0;

        if (latestOpt.isPresent()) {
            MeterReading latest = latestOpt.get();
            electricPrevious = latest.getElectricCurrent();
            waterPrevious = latest.getWaterCurrent();

            // Validate kỳ mới phải sau kỳ cũ nhất
            YearMonth latestMonth = parseBillingMonth(latest.getBillingMonth());
            if (!billingMonth.isAfter(latestMonth)) {
                throw new BadRequestException(
                        "Billing month " + request.getBillingMonth()
                        + " must be after the latest recorded month " + latest.getBillingMonth());
            }

            // Chỉ số mới không được nhỏ hơn kỳ trước
            if (request.getElectricCurrent() < electricPrevious) {
                throw new BadRequestException(
                        "Electric reading (" + request.getElectricCurrent()
                        + ") cannot be less than previous reading (" + electricPrevious + ")");
            }
            if (request.getWaterCurrent() < waterPrevious) {
                throw new BadRequestException(
                        "Water reading (" + request.getWaterCurrent()
                        + ") cannot be less than previous reading (" + waterPrevious + ")");
            }
        }

        MeterReading reading = MeterReading.builder()
                .room(room)
                .billingMonth(request.getBillingMonth())
                .electricCurrent(request.getElectricCurrent())
                .electricPrevious(electricPrevious)
                .waterCurrent(request.getWaterCurrent())
                .waterPrevious(waterPrevious)
                .recordedBy(recorder)
                .note(request.getNote())
                .build();

        return toResponse(meterReadingRepository.save(reading), room);
    }

    @Override
    public MeterReadingResponse getById(Long id) {
        MeterReading reading = meterReadingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter reading not found with id: " + id));
        return toResponse(reading, reading.getRoom());
    }

    @Override
    public Page<MeterReadingResponse> getHistoryByRoom(Long roomId, Pageable pageable) {
        findRoom(roomId); // validate room exists
        return meterReadingRepository
                .findByRoomIdOrderByBillingMonthDesc(roomId, pageable)
                .map(r -> toResponse(r, r.getRoom()));
    }

    @Override
    public Page<MeterReadingResponse> getHistoryByRoomAndYear(Long roomId, int year, Pageable pageable) {
        findRoom(roomId);
        return meterReadingRepository
                .findByRoomIdAndYear(roomId, String.valueOf(year), pageable)
                .map(r -> toResponse(r, r.getRoom()));
    }

    @Override
    public MeterReadingResponse getByRoomAndMonth(Long roomId, String billingMonth) {
        parseBillingMonth(billingMonth); // validate format
        MeterReading reading = meterReadingRepository
                .findByRoomIdAndBillingMonth(roomId, billingMonth)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No meter reading for room " + roomId + " in month " + billingMonth));
        return toResponse(reading, reading.getRoom());
    }

    // --- helpers ---

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private YearMonth parseBillingMonth(String value) {
        try {
            return YearMonth.parse(value);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid billing month format: " + value + ". Expected YYYY-MM");
        }
    }

    private MeterReadingResponse toResponse(MeterReading m, Room room) {
        double electricUsage = m.getElectricUsage();
        double waterUsage = m.getWaterUsage();

        BigDecimal elecPrice = room.getElecPrice();
        BigDecimal waterPrice = room.getWaterPrice();

        BigDecimal electricAmount = elecPrice.multiply(
                BigDecimal.valueOf(electricUsage)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal waterAmount = waterPrice.multiply(
                BigDecimal.valueOf(waterUsage)).setScale(2, RoundingMode.HALF_UP);

        return MeterReadingResponse.builder()
                .id(m.getId())
                .roomId(room.getId())
                .roomTitle(room.getTitle())
                .billingMonth(m.getBillingMonth())
                .electricPrevious(m.getElectricPrevious())
                .electricCurrent(m.getElectricCurrent())
                .electricUsage(electricUsage)
                .electricPrice(elecPrice)
                .electricAmount(electricAmount)
                .waterPrevious(m.getWaterPrevious())
                .waterCurrent(m.getWaterCurrent())
                .waterUsage(waterUsage)
                .waterPrice(waterPrice)
                .waterAmount(waterAmount)
                .note(m.getNote())
                .recordedBy(m.getRecordedBy().getUsername())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
