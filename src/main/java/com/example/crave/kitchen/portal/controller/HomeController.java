package com.example.crave.kitchen.portal.controller;

import com.example.crave.kitchen.portal.service.KitchenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @Autowired
    private KitchenService kitchenService;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", kitchenService.getWelcomeMessage());
        response.put("status", "UP");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}