package com.kma.lamphoun.room_management.entity;

import com.kma.lamphoun.room_management.common.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "invoices",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_invoice_contract_month",
        columnNames = {"contract_id", "billing_month"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_reading_id", nullable = false)
    private MeterReading meterReading;

    /** Kỳ thanh toán YYYY-MM */
    @Column(name = "billing_month", nullable = false, length = 7)
    private String billingMonth;

    /** Snapshot giá thuê phòng */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal rentAmount;

    /** Snapshot: số kWh tiêu thụ */
    @Column(nullable = false)
    private Double electricUsage;

    /** Snapshot: đơn giá điện tại thời điểm lập hóa đơn */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal electricPrice;

    /** Thành tiền điện */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal electricAmount;

    /** Snapshot: số m³ nước tiêu thụ */
    @Column(nullable = false)
    private Double waterUsage;

    /** Snapshot: đơn giá nước */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal waterPrice;

    /** Thành tiền nước */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal waterAmount;

    /** Phí dịch vụ snapshot */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal serviceAmount;

    /** Tổng cộng = rent + electric + water + service */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.UNPAID;

    /** Ngày thanh toán thực tế */
    private LocalDate paidAt;

    /** Hạn thanh toán */
    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
