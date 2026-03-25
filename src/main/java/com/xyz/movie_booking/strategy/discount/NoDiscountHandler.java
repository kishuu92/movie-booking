package com.xyz.movie_booking.strategy.discount;

import com.xyz.movie_booking.dto.BookingRequest;
import org.springframework.stereotype.Component;

@Component
public class NoDiscountHandler extends BaseDiscountHandler {

    @Override
    public double apply(double amount, BookingRequest request) {
        return amount;
    }
}