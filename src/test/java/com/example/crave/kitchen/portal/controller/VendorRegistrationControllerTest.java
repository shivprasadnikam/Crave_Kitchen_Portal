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
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureWebMvc
//class VendorRegistrationControllerTest {
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
//    void testSuccessfulVendorRegistration() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        VendorRegistrationDto dto = createValidVendorRegistrationDto();
//
//        mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message")
//                        .value("Registration successful. Please verify your email to activate your account."))
//                .andExpect(jsonPath("$.data.user.email").value("testvendor@restaurant.com"))
//                .andExpect(jsonPath("$.data.user.name").value("Test Vendor"))
//                .andExpect(jsonPath("$.data.user.role").value("vendor"))
//                .andExpect(jsonPath("$.data.vendorProfile.restaurantName").value("Test Restaurant"))
//                .andExpect(jsonPath("$.data.tokens.accessToken").exists())
//                .andExpect(jsonPath("$.data.tokens.refreshToken").exists())
//                .andExpect(jsonPath("$.data.nextSteps").isArray());
//    }
//
//    @Test
//    void testVendorRegistrationWithInvalidEmail() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        VendorRegistrationDto dto = createValidVendorRegistrationDto();
//        dto.setEmail("invalid-email");
//
//        mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
//                .andExpect(jsonPath("$.errors[?(@.field == 'email')]").exists());
//    }
//
//    @Test
//    void testVendorRegistrationWithWeakPassword() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        VendorRegistrationDto dto = createValidVendorRegistrationDto();
//        dto.setPassword("weak");
//        dto.setConfirmPassword("weak");
//
//        mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
//                .andExpect(jsonPath("$.errors[?(@.field == 'password')]").exists());
//    }
//
//    @Test
//    void testVendorRegistrationWithMismatchedPasswords() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        VendorRegistrationDto dto = createValidVendorRegistrationDto();
//        dto.setConfirmPassword("DifferentPassword123!");
//
//        mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
//                .andExpect(jsonPath("$.errors[?(@.field == 'confirmPassword')]").exists());
//    }
//
//    @Test
//    void testVendorRegistrationWithMissingRequiredFields() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        VendorRegistrationDto dto = new VendorRegistrationDto();
//        dto.setEmail("test@example.com");
//        dto.setPassword("SecurePass123!");
//        dto.setConfirmPassword("SecurePass123!");
//
//        mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
//    }
//
//    @Test
//    void testVendorRegistrationWithInvalidBusinessHours() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        VendorRegistrationDto dto = createValidVendorRegistrationDto();
//        Map<String, VendorRegistrationDto.BusinessHoursDto> businessHours = new HashMap<>();
//
//        VendorRegistrationDto.BusinessHoursDto hours = new VendorRegistrationDto.BusinessHoursDto();
//        hours.setIsOpen(true);
//        hours.setOpenTime("09:00");
//        // Missing close time
//        businessHours.put("monday", hours);
//
//        dto.setBusinessHours(businessHours);
//
//        mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
//                .andExpect(jsonPath("$.errors[?(@.field == 'businessHours.monday.closeTime')]").exists());
//    }
//
//    @Test
//    void testVendorRegistrationWithoutAcceptingTerms() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//
//        VendorRegistrationDto dto = createValidVendorRegistrationDto();
//        dto.setAcceptTerms(false);
//
//        mockMvc.perform(post("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
//                .andExpect(jsonPath("$.errors[?(@.field == 'acceptTerms')]").exists());
//    }
//
//    private VendorRegistrationDto createValidVendorRegistrationDto() {
//        VendorRegistrationDto dto = new VendorRegistrationDto();
//
//        // User information
//        dto.setEmail("testvendor@restaurant.com");
//        dto.setPassword("SecurePass123!");
//        dto.setConfirmPassword("SecurePass123!");
//        dto.setName("Test Vendor");
//
//        // Restaurant information
//        dto.setRestaurantName("Test Restaurant");
//        dto.setPhone("+1234567890");
//        dto.setCuisineType("Italian");
//        dto.setDescription("Authentic Italian cuisine");
//
//        // Address
//        VendorRegistrationDto.AddressDto address = new VendorRegistrationDto.AddressDto();
//        address.setStreet("123 Test Street");
//        address.setCity("Test City");
//        address.setState("TS");
//        address.setZipCode("12345");
//        address.setCountry("USA");
//        dto.setAddress(address);
//
//        // Business hours
//        Map<String, VendorRegistrationDto.BusinessHoursDto> businessHours = new HashMap<>();
//
//        VendorRegistrationDto.BusinessHoursDto mondayHours = new VendorRegistrationDto.BusinessHoursDto();
//        mondayHours.setIsOpen(true);
//        mondayHours.setOpenTime("09:00");
//        mondayHours.setCloseTime("17:00");
//        businessHours.put("monday", mondayHours);
//
//        VendorRegistrationDto.BusinessHoursDto tuesdayHours = new VendorRegistrationDto.BusinessHoursDto();
//        tuesdayHours.setIsOpen(true);
//        tuesdayHours.setOpenTime("09:00");
//        tuesdayHours.setCloseTime("17:00");
//        businessHours.put("tuesday", tuesdayHours);
//
//        dto.setBusinessHours(businessHours);
//
//        // Terms
//        dto.setAcceptTerms(true);
//        dto.setAcceptMarketing(false);
//
//        return dto;
//    }
//}