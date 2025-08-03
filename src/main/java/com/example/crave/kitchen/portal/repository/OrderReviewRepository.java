package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.OrderReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderReviewRepository extends JpaRepository<OrderReviewEntity, Long> {

    // Basic CRUD operations
    Optional<OrderReviewEntity> findByOrderId(Long orderId);

    List<OrderReviewEntity> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<OrderReviewEntity> findByVendorIdOrderByCreatedAtDesc(Long vendorId);

    List<OrderReviewEntity> findByRatingOrderByCreatedAtDesc(Integer rating);

    List<OrderReviewEntity> findByIsAnonymousOrderByCreatedAtDesc(Boolean isAnonymous);

    List<OrderReviewEntity> findByIsVerifiedPurchaseOrderByCreatedAtDesc(Boolean isVerifiedPurchase);

    // Rating range queries
    List<OrderReviewEntity> findByRatingBetweenOrderByCreatedAtDesc(Integer minRating, Integer maxRating);

    List<OrderReviewEntity> findByRatingGreaterThanEqualOrderByCreatedAtDesc(Integer rating);

    List<OrderReviewEntity> findByRatingLessThanEqualOrderByCreatedAtDesc(Integer rating);

    // Date range queries
    List<OrderReviewEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<OrderReviewEntity> findByCustomerIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long customerId,
            LocalDateTime startDate, LocalDateTime endDate);

    List<OrderReviewEntity> findByVendorIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long vendorId,
            LocalDateTime startDate, LocalDateTime endDate);

    // Advanced queries with pagination
    Page<OrderReviewEntity> findByCustomerId(Long customerId, Pageable pageable);

    Page<OrderReviewEntity> findByVendorId(Long vendorId, Pageable pageable);

    Page<OrderReviewEntity> findByRating(Integer rating, Pageable pageable);

    Page<OrderReviewEntity> findByVendorIdAndRating(Long vendorId, Integer rating, Pageable pageable);

    // Search functionality
    @Query("SELECT or FROM OrderReviewEntity or WHERE or.reviewText LIKE %:searchTerm% OR " +
            "or.customer.firstName LIKE %:searchTerm% OR " +
            "or.customer.lastName LIKE %:searchTerm% " +
            "ORDER BY or.createdAt DESC")
    List<OrderReviewEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // Validation methods
    boolean existsByOrderId(Long orderId);

    boolean existsByCustomerIdAndVendorId(Long customerId, Long vendorId);

    // Count methods
    long countByCustomerId(Long customerId);

    long countByVendorId(Long vendorId);

    long countByRating(Integer rating);

    long countByVendorIdAndRating(Long vendorId, Integer rating);

    long countByIsAnonymous(Boolean isAnonymous);

    long countByIsVerifiedPurchase(Boolean isVerifiedPurchase);

    // Rating analytics
    @Query("SELECT AVG(or.rating) FROM OrderReviewEntity or WHERE or.vendorId = :vendorId")
    Double calculateAverageRatingByVendorId(@Param("vendorId") Long vendorId);

    @Query("SELECT AVG(or.rating) FROM OrderReviewEntity or WHERE or.customerId = :customerId")
    Double calculateAverageRatingByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(or) FROM OrderReviewEntity or WHERE or.vendorId = :vendorId")
    long countReviewsByVendorId(@Param("vendorId") Long vendorId);

    @Query("SELECT COUNT(or) FROM OrderReviewEntity or WHERE or.customerId = :customerId")
    long countReviewsByCustomerId(@Param("customerId") Long customerId);

    // Rating distribution
    @Query("SELECT or.rating, COUNT(or) FROM OrderReviewEntity or WHERE or.vendorId = :vendorId GROUP BY or.rating ORDER BY or.rating DESC")
    List<Object[]> findRatingDistributionByVendorId(@Param("vendorId") Long vendorId);

    @Query("SELECT or.rating, COUNT(or) FROM OrderReviewEntity or GROUP BY or.rating ORDER BY or.rating DESC")
    List<Object[]> findOverallRatingDistribution();

    // Recent reviews
    List<OrderReviewEntity> findTop10ByOrderByCreatedAtDesc();

    List<OrderReviewEntity> findTop10ByVendorIdOrderByCreatedAtDesc(Long vendorId);

    List<OrderReviewEntity> findTop10ByCustomerIdOrderByCreatedAtDesc(Long customerId);

    // High and low ratings
    List<OrderReviewEntity> findTop10ByRatingOrderByCreatedAtDesc(Integer rating);

    List<OrderReviewEntity> findTop10ByVendorIdAndRatingOrderByCreatedAtDesc(Long vendorId, Integer rating);

    // Verified purchase reviews
    List<OrderReviewEntity> findByIsVerifiedPurchaseAndVendorIdOrderByCreatedAtDesc(Boolean isVerifiedPurchase,
            Long vendorId);

    List<OrderReviewEntity> findByIsVerifiedPurchaseAndCustomerIdOrderByCreatedAtDesc(Boolean isVerifiedPurchase,
            Long customerId);

    // Anonymous reviews
    List<OrderReviewEntity> findByIsAnonymousAndVendorIdOrderByCreatedAtDesc(Boolean isAnonymous, Long vendorId);

    List<OrderReviewEntity> findByIsAnonymousAndCustomerIdOrderByCreatedAtDesc(Boolean isAnonymous, Long customerId);
}