package com.xyz.movie_booking.dto;

import com.xyz.movie_booking.model.enums.SeatStatus;

public class SeatInfo {

    private String seatNumber;
    private SeatStatus status;

    public SeatInfo(String seatNumber, SeatStatus status) {
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public SeatStatus getStatus() {
        return status;
    }
}