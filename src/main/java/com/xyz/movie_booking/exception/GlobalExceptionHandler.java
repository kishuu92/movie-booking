package com.xyz.movie_booking.exception;

import com.xyz.movie_booking.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // DTO validation failures (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {

        StringBuilder errorMessage = new StringBuilder("Validation failed: ");

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getField())
                        .append(" ")
                        .append(error.getDefaultMessage())
                        .append("; ")
        );

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, errorMessage.toString()));
    }

    // Invalid JSON / type parsing issues (e.g., "abc" for Long)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidJson(HttpMessageNotReadableException ex) {

        log.warn("Invalid request payload", ex);

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, "Invalid request format or data type"));
    }

    // Path/query param type mismatch (e.g., invalid date, number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String paramName = ex.getName();
        Class<?> requiredType = ex.getRequiredType();

        String message;

        if (requiredType == LocalDate.class) {
            message = paramName + " should be in format yyyy-MM-dd";
        } else if (requiredType == Long.class) {
            message = paramName + " should be a valid number";
        } else {
            message = "Invalid value for " + paramName;
        }

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, message));
    }

    // Business validation failures
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    // Missing required field
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(false, null, "Invalid data or missing required fields")
        );
    }

    // Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    // One or more requested seats are already booked.
    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<ApiResponse<Void>> handleSeatConflict(SeatAlreadyBookedException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    // Seats are temporarily locked due to concurrent booking.
    @ExceptionHandler(SeatLockException.class)
    public ResponseEntity<ApiResponse<Void>> handleSeatLock(SeatLockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT) // or LOCKED (423)
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {

        log.error("Invalid system state", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    // Catch-all (unexpected errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {

        log.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, null, "Something went wrong"));
    }
}