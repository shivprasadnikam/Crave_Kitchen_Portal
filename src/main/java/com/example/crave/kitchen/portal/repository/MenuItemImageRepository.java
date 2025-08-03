package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.MenuItemImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemImageRepository extends JpaRepository<MenuItemImageEntity, Long> {

    // Basic CRUD operations
    List<MenuItemImageEntity> findByMenuItemIdOrderByDisplayOrderAsc(Long menuItemId);

    List<MenuItemImageEntity> findByMenuItemIdAndIsPrimaryOrderByDisplayOrderAsc(Long menuItemId, Boolean isPrimary);

    Optional<MenuItemImageEntity> findByMenuItemIdAndIsPrimary(Long menuItemId, Boolean isPrimary);

    @Query("SELECT i FROM MenuItemImageEntity i WHERE i.menuItemId = :menuItemId ORDER BY i.isPrimary DESC, i.displayOrder ASC")
    List<MenuItemImageEntity> findImagesByMenuItemIdOrdered(@Param("menuItemId") Long menuItemId);

    // Primary image management
    @Modifying
    @Query("UPDATE MenuItemImageEntity i SET i.isPrimary = false WHERE i.menuItemId = :menuItemId")
    void clearPrimaryImage(@Param("menuItemId") Long menuItemId);

    @Modifying
    @Query("UPDATE MenuItemImageEntity i SET i.isPrimary = true WHERE i.menuItemId = :menuItemId AND i.id = :imageId")
    int setPrimaryImage(@Param("menuItemId") Long menuItemId, @Param("imageId") Long imageId);

    // Validation methods
    boolean existsByMenuItemIdAndIsPrimary(Long menuItemId, Boolean isPrimary);

    // Count methods
    @Query("SELECT COUNT(i) FROM MenuItemImageEntity i WHERE i.menuItemId = :menuItemId")
    long countImagesByMenuItemId(@Param("menuItemId") Long menuItemId);

    // Delete operations
    void deleteByMenuItemId(Long menuItemId);
}