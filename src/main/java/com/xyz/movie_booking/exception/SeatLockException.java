package com.xyz.movie_booking.exception;

public class SeatLockException extends RuntimeException {
    public SeatLockException(String message) {
        super(message);
    }
}
