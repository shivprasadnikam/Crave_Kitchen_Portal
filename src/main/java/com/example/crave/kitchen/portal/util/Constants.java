package com.example.crave.kitchen.portal.util;

public final class Constants {

    // Application constants
    public static final String APP_NAME = "Crave Kitchen Portal";
    public static final String APP_VERSION = "1.0.0";

    // Security constants
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // API endpoints
    public static final String API_BASE_PATH = "/api/v1";
    public static final String USERS_ENDPOINT = API_BASE_PATH + "/users";
    public static final String RECIPES_ENDPOINT = API_BASE_PATH + "/recipes";

    // Error messages
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";

    // Success messages
    public static final String USER_CREATED_SUCCESS = "User created successfully";
    public static final String USER_UPDATED_SUCCESS = "User updated successfully";

    private Constants() {
        // Private constructor to prevent instantiation
    }
}