package com.xyz.movie_booking.strategy.discount;

import com.xyz.movie_booking.dto.BookingRequest;

public abstract class BaseDiscountHandler implements DiscountHandler {

    protected DiscountHandler next;

    @Override
    public void setNext(DiscountHandler next) {
        this.next = next;
    }

    protected double applyNext(double amount, BookingRequest request) {
        if (next == null) return amount;
        return next.apply(amount, request);
    }
}