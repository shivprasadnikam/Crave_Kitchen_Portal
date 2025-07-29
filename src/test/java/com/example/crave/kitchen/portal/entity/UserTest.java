package com.example.crave.kitchen.portal.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testLombokAnnotations() {
        // Test default constructor
        User user = new User();
        assertNotNull(user);

        // Test setters and getters
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPasswordHash("passwordHash");
        user.setRole(User.UserRole.vendor);

        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
        assertEquals("passwordHash", user.getPasswordHash());
        assertEquals(User.UserRole.vendor, user.getRole());

        // Test additional setters and getters
        user.setEmail("new@example.com");
        user.setName("New Name");
        user.setRole(User.UserRole.admin);

        assertEquals("new@example.com", user.getEmail());
        assertEquals("New Name", user.getName());
        assertEquals(User.UserRole.admin, user.getRole());
    }

    @Test
    void testJpaLifecycleMethods() {
        User user = new User();

        // Test @PrePersist
        user.onCreate();
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());

        // Test @PreUpdate
        user.onUpdate();
        assertNotNull(user.getUpdatedAt());
    }
}