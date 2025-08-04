package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

/**
 * Custom implementation of OrderRepository to handle Oracle pagination
 * compatibility.
 * This implementation uses ROWNUM-based pagination instead of FETCH FIRST
 * syntax
 * for better compatibility with older Oracle versions.
 */
@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find orders by vendor ID with manual pagination using ROWNUM.
     */
    @Override
    public Page<OrderEntity> findByVendorIdWithPagination(Long vendorId, Pageable pageable) {
        // Count total records
        String countQuery = "SELECT COUNT(o) FROM OrderEntity o WHERE o.vendorId = :vendorId";
        Query countQ = entityManager.createQuery(countQuery);
        countQ.setParameter("vendorId", vendorId);
        Long total = (Long) countQ.getSingleResult();

        if (total == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // Get paginated results using ROWNUM
        String dataQuery = "SELECT * FROM (" +
                "  SELECT a.*, ROWNUM rnum FROM (" +
                "    SELECT o.* FROM ck_orders o WHERE o.vendor_id = :vendorId ORDER BY o.created_at DESC" +
                "  ) a WHERE ROWNUM <= :maxRow" +
                ") WHERE rnum > :minRow";

        Query dataQ = entityManager.createNativeQuery(dataQuery, OrderEntity.class);
        dataQ.setParameter("vendorId", vendorId);
        dataQ.setParameter("maxRow", pageable.getOffset() + pageable.getPageSize());
        dataQ.setParameter("minRow", pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<OrderEntity> content = dataQ.getResultList();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * Find orders by vendor ID and status with manual pagination using ROWNUM.
     */
    @Override
    public Page<OrderEntity> findByVendorIdAndOrderStatusWithPagination(Long vendorId,
            OrderEntity.OrderStatus orderStatus,
            Pageable pageable) {
        // Count total records
        String countQuery = "SELECT COUNT(o) FROM OrderEntity o WHERE o.vendorId = :vendorId AND o.orderStatus = :orderStatus";
        Query countQ = entityManager.createQuery(countQuery);
        countQ.setParameter("vendorId", vendorId);
        countQ.setParameter("orderStatus", orderStatus);
        Long total = (Long) countQ.getSingleResult();

        if (total == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // Get paginated results using ROWNUM
        String dataQuery = "SELECT * FROM (" +
                "  SELECT a.*, ROWNUM rnum FROM (" +
                "    SELECT o.* FROM ck_orders o WHERE o.vendor_id = :vendorId AND o.order_status = :orderStatus ORDER BY o.created_at DESC"
                +
                "  ) a WHERE ROWNUM <= :maxRow" +
                ") WHERE rnum > :minRow";

        Query dataQ = entityManager.createNativeQuery(dataQuery, OrderEntity.class);
        dataQ.setParameter("vendorId", vendorId);
        dataQ.setParameter("orderStatus", orderStatus.name());
        dataQ.setParameter("maxRow", pageable.getOffset() + pageable.getPageSize());
        dataQ.setParameter("minRow", pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<OrderEntity> content = dataQ.getResultList();

        return new PageImpl<>(content, pageable, total);
    }
}