//package com.example.crave.kitchen.portal.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureWebMvc
//class AuthControllerTest {
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private MockMvc mockMvc;
//
//    @Test
//    void testRegisterUser() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        UserDto userDto = new UserDto();
//        userDto.setName("Test User");
//        userDto.setEmail("test@example.com");
//        userDto.setPassword("password123");
//        userDto.setRole("VENDOR");
//
//        mockMvc.perform(post("/api/v1/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(userDto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.message").value("User registered successfully"))
//                .andExpect(jsonPath("$.email").value("test@example.com"));
//    }
//
//    @Test
//    void testLoginUser() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        // First register a user
//        UserDto registerDto = new UserDto();
//        registerDto.setName("Login Test User");
//        registerDto.setEmail("login@example.com");
//        registerDto.setPassword("password123");
//        registerDto.setRole("VENDOR");
//
//        mockMvc.perform(post("/api/v1/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerDto)))
//                .andExpect(status().isCreated());
//
//        // Then try to login
//        UserDto loginDto = new UserDto();
//        loginDto.setEmail("login@example.com");
//        loginDto.setPassword("password123");
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(loginDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Login successful"))
//                .andExpect(jsonPath("$.email").value("login@example.com"));
//    }
//
//    @Test
//    void testLoginWithInvalidCredentials() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        UserDto loginDto = new UserDto();
//        loginDto.setEmail("nonexistent@example.com");
//        loginDto.setPassword("wrongpassword");
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(loginDto)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.error").value("Invalid email or password"));
//    }
//
//    @Test
//    void testGetUserProfile() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        // First register a user to get an ID
//        UserDto registerDto = new UserDto();
//        registerDto.setName("Profile Test User");
//        registerDto.setEmail("profile@example.com");
//        registerDto.setPassword("password123");
//        registerDto.setRole("VENDOR");
//
//        String response = mockMvc.perform(post("/api/v1/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(registerDto)))
//                .andExpect(status().isCreated())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        // Extract user ID from response (this is a simplified approach)
//        // In a real test, you might want to parse the JSON response properly
//        mockMvc.perform(get("/api/v1/auth/profile/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").value("profile@example.com"));
//    }
//}