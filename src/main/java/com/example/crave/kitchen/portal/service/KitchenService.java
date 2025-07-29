package com.example.crave.kitchen.portal.service;

import org.springframework.stereotype.Service;

@Service
public class KitchenService {

    public String getWelcomeMessage() {
        return "Welcome to Crave Kitchen Portal!";
    }
}