package com.example.crave.kitchen.portal.exception;

import com.example.crave.kitchen.portal.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleGlobalException(Exception ex, WebRequest request) {
        ApiResponseDto<Object> errorResponse = ApiResponseDto.builder()
                .success(false)
                .message("An unexpected error occurred")
                .error("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .requestId(generateRequestId())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleIllegalArgumentException(IllegalArgumentException ex,
            WebRequest request) {
        ApiResponseDto<Object> errorResponse = ApiResponseDto.builder()
                .success(false)
                .message(ex.getMessage())
                .error("INVALID_ARGUMENT")
                .timestamp(LocalDateTime.now())
                .requestId(generateRequestId())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    private String generateRequestId() {
        return "req_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000);
    }
}