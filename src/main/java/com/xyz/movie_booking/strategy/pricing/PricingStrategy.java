package com.xyz.movie_booking.strategy.pricing;

import com.xyz.movie_booking.model.entity.Show;
import com.xyz.movie_booking.model.entity.ShowSeat;

import java.util.List;
import java.util.Map;

public interface PricingStrategy {

    Map<String, Double> calculateSeatPrices(List<ShowSeat> seats, Show show);
}
