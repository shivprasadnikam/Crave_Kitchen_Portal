package com.example.crave.kitchen.portal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalTime;

@Entity
@Table(name = "ck_business_hours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessHoursEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", precision = 19, scale = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private VendorProfileEntity vendor;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = false;

    @Column(name = "open_time")
    @Temporal(TemporalType.TIME)
    private LocalTime openTime;

    @Column(name = "close_time")
    @Temporal(TemporalType.TIME)
    private LocalTime closeTime;

    @Column(name = "break_start_time")
    @Temporal(TemporalType.TIME)
    private LocalTime breakStartTime;

    @Column(name = "break_end_time")
    @Temporal(TemporalType.TIME)
    private LocalTime breakEndTime;

    @Column(name = "is_break_enabled")
    private Boolean isBreakEnabled = false;

    public enum DayOfWeek {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }
}