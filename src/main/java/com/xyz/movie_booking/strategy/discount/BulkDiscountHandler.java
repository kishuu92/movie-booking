package com.xyz.movie_booking.strategy.discount;

import com.xyz.movie_booking.dto.BookingRequest;
import org.springframework.stereotype.Component;

@Component
public class BulkDiscountHandler extends BaseDiscountHandler {

    @Override
    public double apply(double amount, BookingRequest request) {

        int seatCount = request.getSeatNumbers().size();

        if (seatCount >= 3) {
            amount = amount * 0.8;
        } else if (seatCount == 2) {
            amount = amount * 0.9;
        }

        return applyNext(amount, request);
    }
}