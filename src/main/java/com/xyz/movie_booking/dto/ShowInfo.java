package com.xyz.movie_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ShowInfo {

    private final Long showId;
    private final LocalTime startTime;
    private final Integer availableSeats;
}