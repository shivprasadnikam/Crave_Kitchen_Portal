package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.VendorProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorProfileRepository extends JpaRepository<VendorProfileEntity, Long> {

    boolean existsByRestaurantName(String restaurantName);
}