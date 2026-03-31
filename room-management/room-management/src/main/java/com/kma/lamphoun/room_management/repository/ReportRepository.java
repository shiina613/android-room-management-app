package com.kma.lamphoun.room_management.repository;

import com.kma.lamphoun.room_management.common.enums.ContractStatus;
import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import com.kma.lamphoun.room_management.common.enums.Role;
import com.kma.lamphoun.room_management.common.enums.RoomStatus;
import com.kma.lamphoun.room_management.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Invoice, Long> {

    // ── Revenue ──────────────────────────────────────────────────────────────

    /** Tổng invoice phát sinh theo tháng, lọc theo landlord */
    @Query("""
        SELECT COALESCE(SUM(i.totalAmount), 0)
        FROM Invoice i
        WHERE i.billingMonth = :month
          AND (:landlordId IS NULL OR i.contract.landlord.id = :landlordId)
        """)
    BigDecimal sumInvoicedByMonth(@Param("month") String month,
                                  @Param("landlordId") Long landlordId);

    /** Tổng đã thu (payments) theo tháng paidAt */
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE FUNCTION('DATE_FORMAT', p.paidAt, '%Y-%m') = :month
          AND (:landlordId IS NULL OR p.invoice.contract.landlord.id = :landlordId)
        """)
    BigDecimal sumCollectedByMonth(@Param("month") String month,
                                   @Param("landlordId") Long landlordId);

    /** Số lượng invoice theo tháng + status */
    @Query("""
        SELECT COUNT(i)
        FROM Invoice i
        WHERE i.billingMonth = :month
          AND (:landlordId IS NULL OR i.contract.landlord.id = :landlordId)
          AND (:status IS NULL OR i.status = :status)
        """)
    long countInvoiceByMonthAndStatus(@Param("month") String month,
                                      @Param("landlordId") Long landlordId,
                                      @Param("status") InvoiceStatus status);

    /** Monthly breakdown trong một năm */
    @Query("""
        SELECT i.billingMonth,
               COALESCE(SUM(i.totalAmount), 0),
               COUNT(i)
        FROM Invoice i
        WHERE i.billingMonth LIKE CONCAT(:year, '-%')
          AND (:landlordId IS NULL OR i.contract.landlord.id = :landlordId)
        GROUP BY i.billingMonth
        ORDER BY i.billingMonth ASC
        """)
    List<Object[]> monthlyBreakdown(@Param("year") String year,
                                    @Param("landlordId") Long landlordId);

    /** Tổng payment theo billingMonth của invoice (collected per month) */
    @Query("""
        SELECT i.billingMonth,
               COALESCE(SUM(p.amount), 0)
        FROM Payment p
        JOIN p.invoice i
        WHERE i.billingMonth LIKE CONCAT(:year, '-%')
          AND (:landlordId IS NULL OR i.contract.landlord.id = :landlordId)
        GROUP BY i.billingMonth
        """)
    List<Object[]> monthlyCollected(@Param("year") String year,
                                    @Param("landlordId") Long landlordId);

    // ── Debt ─────────────────────────────────────────────────────────────────

    /** Danh sách invoice chưa thanh toán đủ (UNPAID + OVERDUE) */
    @Query("""
        SELECT i FROM Invoice i
        WHERE i.status IN (com.kma.lamphoun.room_management.common.enums.InvoiceStatus.UNPAID,
                           com.kma.lamphoun.room_management.common.enums.InvoiceStatus.OVERDUE)
          AND (:landlordId IS NULL OR i.contract.landlord.id = :landlordId)
          AND (:month IS NULL OR i.billingMonth = :month)
        ORDER BY i.dueDate ASC
        """)
    List<Invoice> findUnpaidInvoices(@Param("landlordId") Long landlordId,
                                     @Param("month") String month);

    /** Tổng đã trả của từng invoice (dùng để tính remaining) */
    @Query("""
        SELECT p.invoice.id, COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.invoice.id IN :invoiceIds
        GROUP BY p.invoice.id
        """)
    List<Object[]> sumPaidByInvoiceIds(@Param("invoiceIds") List<Long> invoiceIds);

    // ── Room Status ───────────────────────────────────────────────────────────

    @Query("SELECT COUNT(r) FROM Room r WHERE :landlordId IS NULL OR r.owner.id = :landlordId")
    long countRooms(@Param("landlordId") Long landlordId);

    @Query("""
        SELECT COUNT(r) FROM Room r
        WHERE r.status = :status
          AND (:landlordId IS NULL OR r.owner.id = :landlordId)
        """)
    long countRoomsByStatus(@Param("status") RoomStatus status,
                             @Param("landlordId") Long landlordId);

    /** Danh sách phòng OCCUPIED kèm tenant + contract info */
    @Query("""
        SELECT r.id, r.title, r.address,
               u.fullName, u.phone,
               c.startDate, c.endDate
        FROM Contract c
        JOIN c.room r
        JOIN c.tenant u
        WHERE c.status = com.kma.lamphoun.room_management.common.enums.ContractStatus.ACTIVE
          AND (:landlordId IS NULL OR c.landlord.id = :landlordId)
        ORDER BY r.title ASC
        """)
    List<Object[]> findOccupiedRoomDetails(@Param("landlordId") Long landlordId);

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @Query("""
        SELECT COUNT(c) FROM Contract c
        WHERE c.status = com.kma.lamphoun.room_management.common.enums.ContractStatus.ACTIVE
          AND (:landlordId IS NULL OR c.landlord.id = :landlordId)
        """)
    long countActiveContracts(@Param("landlordId") Long landlordId);

    @Query("""
        SELECT COUNT(c) FROM Contract c
        WHERE c.status = com.kma.lamphoun.room_management.common.enums.ContractStatus.ACTIVE
          AND c.endDate BETWEEN :from AND :to
          AND (:landlordId IS NULL OR c.landlord.id = :landlordId)
        """)
    long countExpiringContracts(@Param("from") LocalDate from,
                                @Param("to") LocalDate to,
                                @Param("landlordId") Long landlordId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);
}
