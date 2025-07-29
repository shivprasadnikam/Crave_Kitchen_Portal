package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.VendorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorProfileRepository extends JpaRepository<VendorProfile, Long> {

    Optional<VendorProfile> findByUserId(Long userId);

    List<VendorProfile> findByApprovalStatus(VendorProfile.ApprovalStatus approvalStatus);

    List<VendorProfile> findByIsApproved(Boolean isApproved);

    List<VendorProfile> findByCuisineType(String cuisineType);

    @Query("SELECT vp FROM VendorProfile vp WHERE vp.isApproved = true AND vp.approvalStatus = 'approved'")
    List<VendorProfile> findApprovedVendors();

    @Query("SELECT vp FROM VendorProfile vp WHERE vp.approvalStatus = 'pending'")
    List<VendorProfile> findPendingApprovalVendors();

    @Query("SELECT vp FROM VendorProfile vp WHERE vp.cuisineType = :cuisineType AND vp.isApproved = true")
    List<VendorProfile> findApprovedVendorsByCuisine(@Param("cuisineType") String cuisineType);

    @Query("SELECT vp FROM VendorProfile vp WHERE " +
            "ST_Distance_Sphere(POINT(vp.longitude, vp.latitude), POINT(:longitude, :latitude)) <= :radiusInMeters " +
            "AND vp.isApproved = true")
    List<VendorProfile> findVendorsWithinRadius(@Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusInMeters") double radiusInMeters);

    @Query("SELECT vp FROM VendorProfile vp WHERE " +
            "vp.restaurantName LIKE %:searchTerm% OR vp.cuisineType LIKE %:searchTerm% OR vp.description LIKE %:searchTerm%")
    List<VendorProfile> searchVendors(@Param("searchTerm") String searchTerm);

    boolean existsByUserId(Long userId);

    @Query("SELECT COUNT(vp) FROM VendorProfile vp WHERE vp.approvalStatus = 'pending'")
    long countPendingApprovals();

    @Query("SELECT COUNT(vp) FROM VendorProfile vp WHERE vp.isApproved = true")
    long countApprovedVendors();
}