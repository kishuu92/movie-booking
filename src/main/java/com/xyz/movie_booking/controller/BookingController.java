package com.xyz.movie_booking.controller;

import com.xyz.movie_booking.dto.ApiResponse;
import com.xyz.movie_booking.dto.BookingRequest;
import com.xyz.movie_booking.dto.BookingResponse;
import com.xyz.movie_booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response = bookingService.createBooking(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, response, "Booking successful")
        );
    }
}