package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.VendorsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<VendorsEntity, Long> {
    /**
     * Find user by email
     */
    Optional<VendorsEntity> findByEmail(String email);

}