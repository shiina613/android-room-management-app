package com.kma.lamphoun.room_management.repository;

import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /** Chống trùng invoice cùng contract + tháng */
    boolean existsByContractIdAndBillingMonth(Long contractId, String billingMonth);

    /** Tổng tiền hóa đơn của một contract (chỉ tính UNPAID + PAID, không tính OVERDUE riêng) */
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.contract.id = :contractId")
    BigDecimal sumTotalAmountByContractId(@Param("contractId") Long contractId);

    /** Danh sách invoice theo contract */
    Page<Invoice> findByContractIdOrderByBillingMonthDesc(Long contractId, Pageable pageable);

    /** Danh sách invoice theo tenant (chỉ xem của mình) */
    @Query(value = """
        SELECT i FROM Invoice i
        JOIN FETCH i.contract c
        JOIN FETCH c.room
        JOIN FETCH c.tenant
        JOIN FETCH c.landlord
        WHERE c.tenant.id = :tenantId
          AND (:status IS NULL OR i.status = :status)
        """,
        countQuery = """
        SELECT COUNT(i) FROM Invoice i
        JOIN i.contract c
        WHERE c.tenant.id = :tenantId
          AND (:status IS NULL OR i.status = :status)
        """)
    Page<Invoice> findByTenantId(@Param("tenantId") Long tenantId,
                                 @Param("status") InvoiceStatus status,
                                 Pageable pageable);

    /** Danh sách invoice theo landlord với filter */
    @Query(value = """
        SELECT i FROM Invoice i
        JOIN FETCH i.contract c
        JOIN FETCH c.room r
        JOIN FETCH c.tenant
        JOIN FETCH c.landlord
        WHERE c.landlord.id = :landlordId
          AND (:status IS NULL OR i.status = :status)
          AND (:contractId IS NULL OR c.id = :contractId)
        """,
        countQuery = """
        SELECT COUNT(i) FROM Invoice i
        JOIN i.contract c
        WHERE c.landlord.id = :landlordId
          AND (:status IS NULL OR i.status = :status)
          AND (:contractId IS NULL OR c.id = :contractId)
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
