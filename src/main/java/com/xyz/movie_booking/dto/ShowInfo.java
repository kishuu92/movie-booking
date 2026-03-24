package com.xyz.movie_booking.dto;

import java.time.LocalTime;

public class ShowInfo {

    private Long showId;
    private LocalTime startTime;
    private Integer availableSeats;

    public ShowInfo(Long showId, LocalTime startTime, Integer availableSeats) {
        this.showId = showId;
        this.startTime = startTime;
        this.availableSeats = availableSeats;
    }

    public Long getShowId() {
        return showId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }
}