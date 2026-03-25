package com.xyz.movie_booking.strategy.discount;

import com.xyz.movie_booking.dto.BookingRequest;

public interface DiscountHandler {

    double apply(double amount, BookingRequest request);

    void setNext(DiscountHandler next);
}