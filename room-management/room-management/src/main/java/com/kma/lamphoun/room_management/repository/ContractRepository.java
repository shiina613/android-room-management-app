package com.kma.lamphoun.room_management.repository;

import com.kma.lamphoun.room_management.common.enums.ContractStatus;
import com.kma.lamphoun.room_management.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    /** Kiểm tra phòng đã có hợp đồng ACTIVE chưa */
    boolean existsByRoomIdAndStatus(Long roomId, ContractStatus status);

    /** Lấy hợp đồng ACTIVE của một phòng */
    Optional<Contract> findByRoomIdAndStatus(Long roomId, ContractStatus status);

    /** Danh sách hợp đồng theo landlord */
    @Query("SELECT c FROM Contract c JOIN FETCH c.room r JOIN FETCH r.owner JOIN FETCH c.tenant JOIN FETCH c.landlord WHERE c.landlord.id = :landlordId")
    Page<Contract> findByLandlordId(@Param("landlordId") Long landlordId, Pageable pageable);

    /** Danh sách hợp đồng theo tenant */
    @Query("SELECT c FROM Contract c JOIN FETCH c.room r JOIN FETCH r.owner JOIN FETCH c.tenant JOIN FETCH c.landlord WHERE c.tenant.id = :tenantId")
    Page<Contract> findByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);

    /** Tìm kiếm có filter */
    @Query(value = """
        SELECT c FROM Contract c
        JOIN FETCH c.room r
        JOIN FETCH r.owner
        JOIN FETCH c.tenant
        JOIN FETCH c.landlord
        WHERE (:status IS NULL OR c.status = :status)
          AND (:landlordId IS NULL OR c.landlord.id = :landlordId)
          AND (:tenantId IS NULL OR c.tenant.id = :tenantId)
          AND (:roomId IS NULL OR c.room.id = :roomId)
        """,
        countQuery = """
        SELECT COUNT(c) FROM Contract c
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

    /** Tìm hợp đồng ACTIVE đã quá end_date (dùng cho scheduled job) */
    List<Contract> findByStatusAndEndDateBefore(ContractStatus status, LocalDate date);
}
