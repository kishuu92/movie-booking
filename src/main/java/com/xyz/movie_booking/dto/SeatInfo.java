package com.xyz.movie_booking.dto;

import com.xyz.movie_booking.model.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatInfo {

    private final String seatNumber;
    private final SeatStatus status;
    private final Double price;
}