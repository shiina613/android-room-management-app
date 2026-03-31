package com.kma.lamphoun.room_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(
    name = "meter_readings",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_meter_reading_room_month",
        columnNames = {"room_id", "billing_month"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /**
     * Kỳ ghi chỉ số — lưu dạng "YYYY-MM" (VD: 2026-03)
     * Dùng String để MySQL lưu được, convert qua YearMonth ở service
     */
    @Column(name = "billing_month", nullable = false, length = 7)
    private String billingMonth;

    /** Chỉ số điện kỳ này (kWh) */
    @Column(nullable = false)
    private Double electricCurrent;

    /** Chỉ số điện kỳ trước (kWh) — snapshot để tính tiêu thụ */
    @Column(nullable = false)
    private Double electricPrevious;

    /** Chỉ số nước kỳ này (m³) */
    @Column(nullable = false)
    private Double waterCurrent;

    /** Chỉ số nước kỳ trước (m³) — snapshot */
    @Column(nullable = false)
    private Double waterPrevious;

    /** Người ghi chỉ số */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;

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

    // --- computed helpers (không lưu DB) ---

    @Transient
    public double getElectricUsage() {
        return electricCurrent - electricPrevious;
    }

    @Transient
    public double getWaterUsage() {
        return waterCurrent - waterPrevious;
    }
}
