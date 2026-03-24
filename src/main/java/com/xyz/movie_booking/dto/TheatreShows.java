package com.xyz.movie_booking.dto;

import java.util.List;

public class TheatreShows {

    private Long theatreId;
    private String theatreName;

    private List<ShowInfo> shows;

    public TheatreShows(Long theatreId, String theatreName, List<ShowInfo> shows) {
        this.theatreId = theatreId;
        this.theatreName = theatreName;
        this.shows = shows;
    }

    public Long getTheatreId() {
        return theatreId;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public List<ShowInfo> getShows() {
        return shows;
    }
}
