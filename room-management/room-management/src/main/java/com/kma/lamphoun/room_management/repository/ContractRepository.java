package com.kma.lamphoun.room_management.repository;

import com.kma.lamphoun.room_management.common.enums.ContractStatus;
import com.kma.lamphoun.room_management.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    /** Kiểm tra phòng đã có hợp đồng ACTIVE chưa */
    boolean existsByRoomIdAndStatus(Long roomId, ContractStatus status);

    /** Lấy hợp đồng ACTIVE của một phòng */
    Optional<Contract> findByRoomIdAndStatus(Long roomId, ContractStatus status);

    /** Danh sách hợp đồng theo landlord */
    Page<Contract> findByLandlordId(Long landlordId, Pageable pageable);

    /** Danh sách hợp đồng theo tenant */
    Page<Contract> findByTenantId(Long tenantId, Pageable pageable);

    /** Tìm kiếm có filter */
    @Query("""
        SELECT c FROM Contract c
        WHERE (:status IS NULL OR c.status = :status)
          AND (:landlordId IS NULL OR c.landlord.id = :landlordId)
          AND (:tenantId IS NULL OR c.tenant.id = :tenantId)
          AND (:roomId IS NULL OR c.room.id = :roomId)
        """)
    Page<Contract> search(@Param("status") ContractStatus status,
                          @Param("landlordId") Long landlordId,
                          @Param("tenantId") Long tenantId,
                          @Param("roomId") Long roomId,
                          Pageable pageable);
}
