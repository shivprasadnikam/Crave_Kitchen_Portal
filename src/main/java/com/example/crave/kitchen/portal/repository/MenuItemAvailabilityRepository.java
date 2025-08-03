package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.MenuItemAvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemAvailabilityRepository extends JpaRepository<MenuItemAvailabilityEntity, Long> {

        // Basic CRUD operations
        List<MenuItemAvailabilityEntity> findByMenuItemId(Long menuItemId);

        List<MenuItemAvailabilityEntity> findByMenuItemIdAndIsAvailable(Long menuItemId, Boolean isAvailable);

        Optional<MenuItemAvailabilityEntity> findByMenuItemIdAndDayOfWeek(Long menuItemId, Integer dayOfWeek);

        // Special offers
        @Query("SELECT a FROM MenuItemAvailabilityEntity a WHERE a.menuItemId = :menuItemId AND a.isAvailable = true AND a.isSpecialOffer = true AND "
                        +
                        "a.specialOfferValidFrom <= :currentDate AND a.specialOfferValidUntil >= :currentDate")
        List<MenuItemAvailabilityEntity> findActiveSpecialOffersByMenuItemId(@Param("menuItemId") Long menuItemId,
                        @Param("currentDate") LocalDateTime currentDate);

        @Query("SELECT a FROM MenuItemAvailabilityEntity a WHERE a.menuItemId = :menuItemId AND a.dayOfWeek = :dayOfWeek AND a.isAvailable = true")
        Optional<MenuItemAvailabilityEntity> findAvailableByMenuItemIdAndDayOfWeek(
                        @Param("menuItemId") Long menuItemId,
                        @Param("dayOfWeek") Integer dayOfWeek);

        // Validation methods
        boolean existsByMenuItemIdAndDayOfWeek(Long menuItemId, Integer dayOfWeek);

        // Delete operations
        void deleteByMenuItemId(Long menuItemId);
}