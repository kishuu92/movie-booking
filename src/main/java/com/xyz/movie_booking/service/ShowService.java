package com.xyz.movie_booking.service;

import com.xyz.movie_booking.dto.*;
import com.xyz.movie_booking.exception.ResourceNotFoundException;
import com.xyz.movie_booking.model.entity.Movie;
import com.xyz.movie_booking.model.entity.Show;
import com.xyz.movie_booking.model.entity.ShowSeat;
import com.xyz.movie_booking.repository.MovieRepository;
import com.xyz.movie_booking.repository.ShowRepository;
import com.xyz.movie_booking.repository.ShowSeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ShowService {

    private static final Logger log = LoggerFactory.getLogger(ShowService.class);

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ShowSeatRepository showSeatRepository;

    public ShowService(ShowRepository showRepository, MovieRepository movieRepository, ShowSeatRepository showSeatRepository) {
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.showSeatRepository = showSeatRepository;
    }

    public ShowResponse getShows(Long movieId, String city, LocalDate date) {

        // Validate date is not in the past
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the past");
        }

        // Validate movie exists
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        String movieName = movie.getName();

        // Fetch shows
        log.info("Fetching shows for movieId={}, city={}, date={}", movieId, city, date);
        city = city.trim().toLowerCase();
        List<Show> shows = showRepository.findShows(movieId, city, date);

        // Empty shows → valid case
        if (shows.isEmpty()) {
            return new ShowResponse(movieId, movieName, city, new ArrayList<>());
        }

        return mapToResponse(shows, movieId, movieName, city);
    }

    public SeatResponse getSeats(Long showId) {

        // Validate show exists
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        // Fetch seats
        log.info("Fetching show seats for showId={}", showId);
        List<ShowSeat> seats = showSeatRepository.findByShowId(showId);

        // Map
        if (seats.isEmpty()) {
            log.warn("Seat map missing for showId={}", showId);
        }
        List<SeatInfo> seatInfos = seats.stream()
                .map(s -> new SeatInfo(s.getSeatNumber(), s.getStatus()))
                .toList();

        return new SeatResponse(showId, seatInfos);
    }

    private ShowResponse mapToResponse(List<Show> shows, Long movieId, String movieName, String city) {

        Map<Long, TheatreShows> theatreMap = new HashMap<>();

        for (Show show : shows) {

            Long theatreId = show.getTheatre().getId();

            theatreMap.putIfAbsent(
                    theatreId,
                    new TheatreShows(theatreId, show.getTheatre().getName(), new ArrayList<>())
            );

            TheatreShows theatreShows = theatreMap.get(theatreId);

            theatreShows.getShows().add(
                    new ShowInfo(show.getId(), show.getStartTime(), show.getAvailableSeats())
            );
        }

        return new ShowResponse(movieId, movieName, city, new ArrayList<>(theatreMap.values()));
    }
}
