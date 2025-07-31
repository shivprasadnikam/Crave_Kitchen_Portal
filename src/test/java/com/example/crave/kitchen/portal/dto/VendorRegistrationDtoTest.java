//package com.example.crave.kitchen.portal.dto;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class VendorRegistrationDtoTest {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    void testVendorRegistrationDtoSerialization() throws Exception {
//        // Create the DTO with the example data
//        VendorRegistrationDto dto = createExampleVendorRegistrationDto();
//
//        // Serialize to JSON
//        String json = objectMapper.writeValueAsString(dto);
//
//        // Verify the JSON contains expected fields
//        assertTrue(json.contains("maria@tacohaven.com"));
//        assertTrue(json.contains("Maria Gonzales"));
//        assertTrue(json.contains("Taco Haven"));
//        assertTrue(json.contains("Mexican"));
//        assertTrue(json.contains("123 Fiesta Lane"));
//        assertTrue(json.contains("Austin"));
//        assertTrue(json.contains("TX"));
//        assertTrue(json.contains("73301"));
//        assertTrue(json.contains("monday"));
//        assertTrue(json.contains("09:00"));
//        assertTrue(json.contains("17:00"));
//        assertTrue(json.contains("true")); // acceptTerms
//        assertTrue(json.contains("false")); // acceptMarketing
//    }
//
//    @Test
//    void testVendorRegistrationDtoDeserialization() throws Exception {
//        // Create the JSON string
//        String json = """
//                {
//                  "email": "maria@tacohaven.com",
//                  "password": "SecurePass123!",
//                  "confirmPassword": "SecurePass123!",
//                  "name": "Maria Gonzales",
//                  "restaurantName": "Taco Haven",
//                  "phone": "+1234567890",
//                  "address": {
//                    "street": "123 Fiesta Lane",
//                    "city": "Austin",
//                    "state": "TX",
//                    "zipCode": "73301",
//                    "country": "USA"
//                  },
//                  "businessHours": {
//                    "monday": {
//                      "isOpen": true,
//                      "openTime": "09:00",
//                      "closeTime": "17:00"
//                    },
//                    "tuesday": {
//                      "isOpen": true,
//                      "openTime": "09:00",
//                      "closeTime": "17:00"
//                    },
//                    "wednesday": {
//                      "isOpen": false,
//                      "openTime": null,
//                      "closeTime": null
//                    }
//                  },
//                  "cuisineType": "Mexican",
//                  "description": "Authentic Mexican street food served fresh daily.",
//                  "acceptTerms": true,
//                  "acceptMarketing": false
//                }
//                """;
//
//        // Deserialize from JSON
//        VendorRegistrationDto dto = objectMapper.readValue(json, VendorRegistrationDto.class);
//
//        // Verify the DTO contains expected values
//        assertEquals("maria@tacohaven.com", dto.getEmail());
//        assertEquals("SecurePass123!", dto.getPassword());
//        assertEquals("SecurePass123!", dto.getConfirmPassword());
//        assertEquals("Maria Gonzales", dto.getName());
//        assertEquals("Taco Haven", dto.getRestaurantName());
//        assertEquals("+1234567890", dto.getPhone());
//        assertEquals("Mexican", dto.getCuisineType());
//        assertEquals("Authentic Mexican street food served fresh daily.", dto.getDescription());
//        assertTrue(dto.getAcceptTerms());
//        assertFalse(dto.getAcceptMarketing());
//
//        // Verify address
//        assertNotNull(dto.getAddress());
//        assertEquals("123 Fiesta Lane", dto.getAddress().getStreet());
//        assertEquals("Austin", dto.getAddress().getCity());
//        assertEquals("TX", dto.getAddress().getState());
//        assertEquals("73301", dto.getAddress().getZipCode());
//        assertEquals("USA", dto.getAddress().getCountry());
//
//        // Verify business hours
//        assertNotNull(dto.getBusinessHours());
//        assertEquals(3, dto.getBusinessHours().size());
//
//        // Monday
//        VendorRegistrationDto.BusinessHoursDto monday = dto.getBusinessHours().get("monday");
//        assertNotNull(monday);
//        assertTrue(monday.getIsOpen());
//        assertEquals("09:00", monday.getOpenTime());
//        assertEquals("17:00", monday.getCloseTime());
//
//        // Tuesday
//        VendorRegistrationDto.BusinessHoursDto tuesday = dto.getBusinessHours().get("tuesday");
//        assertNotNull(tuesday);
//        assertTrue(tuesday.getIsOpen());
//        assertEquals("09:00", tuesday.getOpenTime());
//        assertEquals("17:00", tuesday.getCloseTime());
//
//        // Wednesday (closed)
//        VendorRegistrationDto.BusinessHoursDto wednesday = dto.getBusinessHours().get("wednesday");
//        assertNotNull(wednesday);
//        assertFalse(wednesday.getIsOpen());
//        assertNull(wednesday.getOpenTime());
//        assertNull(wednesday.getCloseTime());
//    }
//
//    @Test
//    void testVendorRegistrationDtoBuilder() {
//        VendorRegistrationDto dto = createExampleVendorRegistrationDto();
//
//        // Verify all fields are set correctly
//        assertEquals("maria@tacohaven.com", dto.getEmail());
//        assertEquals("SecurePass123!", dto.getPassword());
//        assertEquals("Maria Gonzales", dto.getName());
//        assertEquals("Taco Haven", dto.getRestaurantName());
//        assertEquals("Mexican", dto.getCuisineType());
//        assertTrue(dto.getAcceptTerms());
//        assertFalse(dto.getAcceptMarketing());
//
//        // Verify nested objects
//        assertNotNull(dto.getAddress());
//        assertNotNull(dto.getBusinessHours());
//        assertEquals(1, dto.getBusinessHours().size());
//    }
//
//    private VendorRegistrationDto createExampleVendorRegistrationDto() {
//        VendorRegistrationDto dto = new VendorRegistrationDto();
//
//        // User information
//        dto.setEmail("maria@tacohaven.com");
//        dto.setPassword("SecurePass123!");
//        dto.setConfirmPassword("SecurePass123!");
//        dto.setName("Maria Gonzales");
//
//        // Restaurant information
//        dto.setRestaurantName("Taco Haven");
//        dto.setPhone("+1234567890");
//        dto.setCuisineType("Mexican");
//        dto.setDescription("Authentic Mexican street food served fresh daily.");
//
//        // Address
//        VendorRegistrationDto.AddressDto address = new VendorRegistrationDto.AddressDto();
//        address.setStreet("123 Fiesta Lane");
//        address.setCity("Austin");
//        address.setState("TX");
//        address.setZipCode("73301");
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
//        dto.setBusinessHours(businessHours);
//
//        // Terms and marketing
//        dto.setAcceptTerms(true);
//        dto.setAcceptMarketing(false);
//
//        return dto;
//    }
//}