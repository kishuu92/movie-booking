package com.xyz.movie_booking.dto;

import java.util.List;

public class SeatResponse {

    private Long showId;
    private List<SeatInfo> seats;

    public SeatResponse(Long showId, List<SeatInfo> seats) {
        this.showId = showId;
        this.seats = seats;
    }

    public Long getShowId() {
        return showId;
    }

    public List<SeatInfo> getSeats() {
        return seats;
    }
}


