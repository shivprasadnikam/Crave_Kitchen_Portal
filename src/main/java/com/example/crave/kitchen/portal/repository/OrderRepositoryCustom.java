package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom interface for OrderRepository to define custom pagination methods
 * that handle Oracle compatibility issues.
 */
public interface OrderRepositoryCustom {

    /**
     * Find orders by vendor ID with manual pagination using ROWNUM.
     */
    Page<OrderEntity> findByVendorIdWithPagination(Long vendorId, Pageable pageable);

    /**
     * Find orders by vendor ID and status with manual pagination using ROWNUM.
     */
    Page<OrderEntity> findByVendorIdAndOrderStatusWithPagination(Long vendorId,
            OrderEntity.OrderStatus orderStatus,
            Pageable pageable);
}