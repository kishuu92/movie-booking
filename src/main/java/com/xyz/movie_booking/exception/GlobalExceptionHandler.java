package com.xyz.movie_booking.exception;

import com.xyz.movie_booking.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(IllegalArgumentException ex) {

        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {

        StringBuilder errorMessage = new StringBuilder("Validation failed: ");

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append("Field '")
                    .append(error.getField())
                    .append("' ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });

        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, errorMessage.toString()));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {

        log.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, null, "Something went wrong"));
    }
}
