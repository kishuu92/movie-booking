package com.xyz.movie_booking.dto;

import java.util.List;

public class ShowResponse {

    private Long movieId;
    private String movieName;
    private String city;

    private List<TheatreShows> theatres;

    public ShowResponse(Long movieId, String movieName, String city, List<TheatreShows> theatres) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.city = city;
        this.theatres = theatres;
    }

    public Long getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getCity() {
        return city;
    }

    public List<TheatreShows> getTheatres() {
        return theatres;
    }
}