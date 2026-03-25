package com.xyz.movie_booking.service;

import com.xyz.movie_booking.dto.*;
import com.xyz.movie_booking.exception.ResourceNotFoundException;
import com.xyz.movie_booking.model.entity.Movie;
import com.xyz.movie_booking.model.entity.Show;
import com.xyz.movie_booking.model.entity.ShowSeat;
import com.xyz.movie_booking.repository.MovieRepository;
import com.xyz.movie_booking.repository.ShowRepository;
import com.xyz.movie_booking.repository.ShowSeatRepository;
import com.xyz.movie_booking.strategy.pricing.PricingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShowService {

    private static final Logger log = LoggerFactory.getLogger(ShowService.class);

    private final PricingStrategy pricingStrategy;
    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ShowSeatRepository showSeatRepository;

    public ShowService(PricingStrategy pricingStrategy,
                       ShowRepository showRepository,
                       MovieRepository movieRepository,
                       ShowSeatRepository showSeatRepository) {
        this.pricingStrategy = pricingStrategy;
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.showSeatRepository = showSeatRepository;
    }

    public ShowResponse getShows(Long movieId, String city, LocalDate date) {

        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            throw new IllegalArgumentException("Date cannot be in the past");
        }

        String movieName = movieRepository.findById(movieId)
                .map(Movie::getName)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        log.info("Fetching shows for movieId={}, city={}, date={}", movieId, city, date);

        List<Show> shows = showRepository.findShows(movieId, city, date);

        if (shows.isEmpty()) {
            return new ShowResponse(movieId, movieName, city, new ArrayList<>());
        }

        return mapToResponse(shows, movieId, movieName, city);
    }

    public SeatResponse getSeats(Long showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<ShowSeat> seats = showSeatRepository.findByShowId(showId);

        if (seats.isEmpty()) {
            log.error("Seat map missing for showId={}", showId);
            throw new IllegalStateException("Seat configuration missing for this show");
        }

        log.info("Fetching show seats for showId={}", showId);

        Map<String, Double> priceMap =
                pricingStrategy.calculateSeatPrices(seats, show);

        List<SeatInfo> seatInfos = seats.stream()
                .map(s -> new SeatInfo(s.getSeatNumber(), s.getStatus(), priceMap.get(s.getSeatNumber())))
                .toList();

        return new SeatResponse(showId, seatInfos);
    }

    private ShowResponse mapToResponse(List<Show> shows, Long movieId, String movieName, String city) {

        Map<Long, TheatreShows> theatreMap = new LinkedHashMap<>();

        for (Show show : shows) {

            var theatre = show.getTheatre();
            Long theatreId = theatre.getId();

            theatreMap.putIfAbsent(
                    theatreId,
                    new TheatreShows(theatreId, theatre.getName(), new ArrayList<>())
            );

            theatreMap.get(theatreId).getShows().add(
                    new ShowInfo(show.getId(), show.getStartTime(), show.getAvailableSeats())
            );
        }

        return new ShowResponse(movieId, movieName, city, new ArrayList<>(theatreMap.values()));
    }
}