package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.OrderPromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderPromotionRepository extends JpaRepository<OrderPromotionEntity, Long> {


}