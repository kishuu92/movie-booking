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

    @NotNull
    private Long userId;

    @NotNull
    private Long showId;

    @NotEmpty
    private List<String> seatNumbers;
}