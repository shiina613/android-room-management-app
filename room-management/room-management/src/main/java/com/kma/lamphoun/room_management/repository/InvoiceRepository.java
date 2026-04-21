package com.kma.lamphoun.room_management.repository;

import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /** Chống trùng invoice cùng contract + tháng */
    boolean existsByContractIdAndBillingMonth(Long contractId, String billingMonth);

    /** Danh sách invoice theo contract */
    Page<Invoice> findByContractIdOrderByBillingMonthDesc(Long contractId, Pageable pageable);

    /** Danh sách invoice theo tenant (chỉ xem của mình) */
    @Query("""
        SELECT i FROM Invoice i
        WHERE i.contract.tenant.id = :tenantId
          AND (:status IS NULL OR i.status = :status)
        ORDER BY i.billingMonth DESC
        """)
    Page<Invoice> findByTenantId(@Param("tenantId") Long tenantId,
                                 @Param("status") InvoiceStatus status,
                                 Pageable pageable);

    /** Danh sách invoice theo landlord với filter */
    @Query("""
        SELECT i FROM Invoice i
        WHERE i.contract.landlord.id = :landlordId
          AND (:status IS NULL OR i.status = :status)
          AND (:contractId IS NULL OR i.contract.id = :contractId)
        ORDER BY i.billingMonth DESC
        """)
    Page<Invoice> findByLandlordId(@Param("landlordId") Long landlordId,
                                   @Param("status") InvoiceStatus status,
                                   @Param("contractId") Long contractId,
                                   Pageable pageable);

    /** Tìm invoice UNPAID đã quá due_date (dùng cho scheduled job) */
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);

    /** Tìm invoice UNPAID sắp đến hạn đúng ngày (dùng cho reminder) */
    List<Invoice> findByStatusAndDueDate(InvoiceStatus status, LocalDate date);
}
