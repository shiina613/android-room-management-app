package com.kma.lamphoun.room_management.repository;

import com.kma.lamphoun.room_management.common.enums.RoomCategory;
import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import com.kma.lamphoun.room_management.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Page<Room> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Room> findByStatus(RoomStatus status, Pageable pageable);

    Page<Room> findByCategory(RoomCategory category, Pageable pageable);

    @Query("""
        SELECT r FROM Room r
        WHERE (:status IS NULL OR r.status = :status)
          AND (:category IS NULL OR r.category = :category)
          AND (:keyword IS NULL
               OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(r.address) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Room> search(@Param("status") RoomStatus status,
                      @Param("category") RoomCategory category,
                      @Param("keyword") String keyword,
                      Pageable pageable);
}
