package com.xyz.movie_booking.dto;

import com.xyz.movie_booking.model.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookingResponse {

    private final Long bookingId;
    private final BookingStatus status;
    private final Double totalAmount;
    private final String pollingUrl;
    private List<String> seats;
}
