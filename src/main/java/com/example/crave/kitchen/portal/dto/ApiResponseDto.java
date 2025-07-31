package com.example.crave.kitchen.portal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {

    private Boolean success;
    private String message;
    private String error;
    private LocalDateTime timestamp;
    private String requestId;
    private T data;
    private List<ValidationErrorDto> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorDto {
        private String field;
        private String message;
        private String code;
    }

    // Static factory methods for common responses
    public static <T> ApiResponseDto<T> success(String message, T data) {
        return ApiResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseDto<T> error(String message, String error) {
        return ApiResponseDto.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponseDto<T> validationError(String message, List<ValidationErrorDto> errors) {
        return ApiResponseDto.<T>builder()
                .success(false)
                .message(message)
                .error("VALIDATION_ERROR")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}