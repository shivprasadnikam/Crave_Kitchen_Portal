package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    // Basic CRUD operations
    Optional<CustomerEntity> findByUserId(String userId);

    Optional<CustomerEntity> findByEmail(String email);

    Optional<CustomerEntity> findByPhoneNumber(String phoneNumber);

    List<CustomerEntity> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);

    // Search functionality
    @Query("SELECT c FROM CustomerEntity c WHERE c.isActive = true AND " +
            "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY c.createdAt DESC")
    List<CustomerEntity> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // Validation methods
    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUserIdAndIdNot(String userId, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    // Count methods
    long countByIsActive(Boolean isActive);

    @Query("SELECT COUNT(c) FROM CustomerEntity c WHERE c.isActive = true")
    long countActiveCustomers();

    // Find by gender
    List<CustomerEntity> findByGenderAndIsActiveOrderByCreatedAtDesc(CustomerEntity.Gender gender, Boolean isActive);

    long countByGenderAndIsActive(CustomerEntity.Gender gender, Boolean isActive);
}