package com.xyz.movie_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ShowResponse {

    private Long movieId;
    private String movieName;
    private String city;
    private List<TheatreShows> theatres;
}