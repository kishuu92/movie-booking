package com.xyz.movie_booking.strategy.pricing;

import com.xyz.movie_booking.model.entity.Show;
import com.xyz.movie_booking.model.entity.ShowSeat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SurgePricingStrategy implements PricingStrategy {

    @Override
    public Map<String, Double> calculateSeatPrices(List<ShowSeat> seats, Show show) {

        boolean surge = show.getAvailableSeats() < 10;

        return seats.stream()
                .collect(Collectors.toMap(
                        ShowSeat::getSeatNumber,
                        s -> surge ? s.getPrice() * 1.2 : s.getPrice()
                ));
    }
}