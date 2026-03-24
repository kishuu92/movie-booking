package com.xyz.movie_booking.dto;

import com.xyz.movie_booking.model.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingResponse {

    private final Long bookingId;
    private final BookingStatus status;
    private final Double totalAmount;
}
