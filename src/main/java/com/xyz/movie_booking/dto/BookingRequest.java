package com.xyz.movie_booking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "showId is required")
    private Long showId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<String> seatNumbers;
}