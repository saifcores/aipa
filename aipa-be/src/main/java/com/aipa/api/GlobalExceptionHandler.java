package com.aipa.api;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.aipa.api.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidation(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                String message = ex.getBindingResult().getFieldErrors().stream()
                                .findFirst()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .orElse("Validation failed");
                return build(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
        }

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ApiErrorResponse> handleStatus(
                        ResponseStatusException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
                return build(status, ex.getReason() == null ? status.getReasonPhrase()
                                : ex.getReason(), request.getRequestURI());
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
                        IllegalArgumentException ex,
                        HttpServletRequest request) {
                return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handleGeneric(
                        Exception ex,
                        HttpServletRequest request) {
                org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class)
                                .error("Unhandled error on {}", request.getRequestURI(), ex);
                return build(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Unexpected server error",
                                request.getRequestURI());
        }

        private ResponseEntity<ApiErrorResponse> build(
                        HttpStatus status,
                        String message,
                        String path) {
                return ResponseEntity.status(status).body(new ApiErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                path));
        }
}
