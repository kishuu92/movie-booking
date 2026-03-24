package com.xyz.movie_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SeatResponse {

    private final Long showId;
    private final List<SeatInfo> seats;
}


