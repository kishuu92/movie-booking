package com.xyz.movie_booking.controller;

import com.xyz.movie_booking.dto.ApiResponse;
import com.xyz.movie_booking.dto.SeatResponse;
import com.xyz.movie_booking.dto.ShowResponse;
import com.xyz.movie_booking.service.ShowService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/shows")
@Validated
public class ShowController {

    private static final Logger log = LoggerFactory.getLogger(ShowController.class);

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ShowResponse>> getShows(
            @RequestParam @NotNull Long movieId,
            @RequestParam @NotBlank String city,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Fetching shows for movieId={}, city={}, date={}", movieId, city, date);

        ShowResponse response = showService.getShows(movieId, city, date);

        boolean hasShows = response.getTheatres() != null && !response.getTheatres().isEmpty();

        String message = hasShows
                ? "Shows fetched successfully"
                : "No shows available";

        return ResponseEntity.ok(new ApiResponse<>(true, response, message));
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<ApiResponse<SeatResponse>> getSeats(@PathVariable @NotNull @Positive Long showId) {

        log.info("Fetching show seats for showId={}", showId);

        SeatResponse response = showService.getSeats(showId);

        return ResponseEntity.ok(new ApiResponse<>(true, response, "Seats fetched successfully"));
    }
}
