package com.kma.lamphoun.room_management.repository;

import com.kma.lamphoun.room_management.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /** Tổng tiền đã thanh toán cho một invoice */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.invoice.id = :invoiceId")
    BigDecimal sumAmountByInvoiceId(@Param("invoiceId") Long invoiceId);

    /** Danh sách payment theo invoice */
    Page<Payment> findByInvoiceIdOrderByPaidAtDesc(Long invoiceId, Pageable pageable);

    /** Danh sách payment theo tenant */
    @Query("""
        SELECT p FROM Payment p
        WHERE p.invoice.contract.tenant.id = :tenantId
        ORDER BY p.paidAt DESC
        """)
    Page<Payment> findByTenantId(@Param("tenantId") Long tenantId, Pageable pageable);

    /** Danh sách payment theo landlord */
    @Query("""
        SELECT p FROM Payment p
        WHERE p.invoice.contract.landlord.id = :landlordId
        ORDER BY p.paidAt DESC
        """)
    Page<Payment> findByLandlordId(@Param("landlordId") Long landlordId, Pageable pageable);
}
