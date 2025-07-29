package com.example.crave.kitchen.portal.repository;

import com.example.crave.kitchen.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    Optional<User> findByEmailVerificationToken(String token);

    List<User> findByRole(User.UserRole role);

    List<User> findByIsActive(Boolean isActive);

    List<User> findByIsEmailVerified(Boolean isEmailVerified);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true AND u.isEmailVerified = true")
    Optional<User> findActiveVerifiedUserByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.emailVerificationToken = :token AND u.emailVerificationExpiresAt > :now")
    Optional<User> findValidEmailVerificationToken(@Param("token") String token, @Param("now") LocalDateTime now);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(@Param("role") User.UserRole role);

    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :date")
    List<User> findUsersNotLoggedInSince(@Param("date") LocalDateTime date);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIsActiveTrue(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveUsersByRole(@Param("role") User.UserRole role);
}