package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.dto.request.MeterReadingRequest;
import com.kma.lamphoun.room_management.dto.response.MeterReadingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeterReadingService {

    /** Ghi chỉ số mới cho kỳ tháng */
    MeterReadingResponse record(String recorderUsername, MeterReadingRequest request);

    /** Xem chi tiết một bản ghi */
    MeterReadingResponse getById(Long id);

    /** Lịch sử chỉ số theo phòng (phân trang) */
    Page<MeterReadingResponse> getHistoryByRoom(Long roomId, Pageable pageable);

    /** Lịch sử theo phòng + năm */
    Page<MeterReadingResponse> getHistoryByRoomAndYear(Long roomId, int year, Pageable pageable);

    /** Lấy chỉ số theo phòng + kỳ cụ thể */
    MeterReadingResponse getByRoomAndMonth(Long roomId, String billingMonth);
}
