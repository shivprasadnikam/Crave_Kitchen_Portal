package com.example.crave.kitchen.portal.controller;

import com.example.crave.kitchen.portal.dto.UserDto;
import com.example.crave.kitchen.portal.service.KitchenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Autowired
    private KitchenService kitchenService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", kitchenService.getWelcomeMessage());
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        // This is a placeholder implementation
        // In a real application, you would save the user to the database
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Users endpoint - requires authentication");
        response.put("total", 0);
        return ResponseEntity.ok(response);
    }
}