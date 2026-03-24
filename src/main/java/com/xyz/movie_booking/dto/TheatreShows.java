package com.xyz.movie_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TheatreShows {

    private Long theatreId;
    private String theatreName;
    private List<ShowInfo> shows;
}
