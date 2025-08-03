package com.example.crave.kitchen.portal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ck_order_promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPromotionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_promotion_seq")
    @SequenceGenerator(name = "order_promotion_seq", sequenceName = "seq_ck_order_promotions_id", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private PromotionEntity promotion;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "applied_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime appliedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }
}