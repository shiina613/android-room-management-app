package com.kma.lamphoun.room_management.repository;

import com.kma.lamphoun.room_management.entity.MeterReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

    /** Kiểm tra đã có chỉ số kỳ này chưa */
    boolean existsByRoomIdAndBillingMonth(Long roomId, String billingMonth);

    /** Lấy chỉ số kỳ gần nhất của phòng để làm previous */
    @Query("""
        SELECT m FROM MeterReading m
        WHERE m.room.id = :roomId
        ORDER BY m.billingMonth DESC
        LIMIT 1
        """)
    Optional<MeterReading> findLatestByRoomId(@Param("roomId") Long roomId);

    /** Lấy chỉ số theo phòng + kỳ cụ thể */
    Optional<MeterReading> findByRoomIdAndBillingMonth(Long roomId, String billingMonth);

    /** Lịch sử chỉ số theo phòng, sắp xếp mới nhất trước */
    Page<MeterReading> findByRoomIdOrderByBillingMonthDesc(Long roomId, Pageable pageable);

    /** Lịch sử theo năm */
    @Query("""
        SELECT m FROM MeterReading m
        WHERE m.room.id = :roomId
          AND m.billingMonth LIKE CONCAT(:year, '-%')
        ORDER BY m.billingMonth ASC
        """)
    Page<MeterReading> findByRoomIdAndYear(@Param("roomId") Long roomId,
                                           @Param("year") String year,
                                           Pageable pageable);
}
