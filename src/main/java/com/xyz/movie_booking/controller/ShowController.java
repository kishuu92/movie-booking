package com.xyz.movie_booking.controller;

import com.xyz.movie_booking.dto.ApiResponse;
import com.xyz.movie_booking.dto.SeatResponse;
import com.xyz.movie_booking.dto.ShowResponse;
import com.xyz.movie_booking.service.ShowService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/shows")
@Validated
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ShowResponse>> getShows(
            @RequestParam @NotNull Long movieId,
            @RequestParam @NotBlank String city,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        ShowResponse response = showService.getShows(movieId, city, date);

        String message = response.getTheatres().isEmpty()
                ? "No shows available"
                : "Shows fetched successfully";

        return ResponseEntity.ok(
                new ApiResponse<>(true, response, message)
        );
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<ApiResponse<SeatResponse>> getSeats(@PathVariable Long showId) {

        SeatResponse response = showService.getSeats(showId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, response, "Seats fetched successfully")
        );
    }
}
