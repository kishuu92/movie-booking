package com.xyz.movie_booking.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "shows",
        indexes = {
                @Index(name = "idx_movie_city_date", columnList = "movie_id, show_date"),
                @Index(name = "idx_theatre", columnList = "theatre_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"movie_id", "theatre_id", "show_date", "start_time"})
        }
)
@NoArgsConstructor
@Getter
@Setter
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    private LocalDate showDate;
    private LocalTime startTime;

    private Integer totalSeats;
    private Integer availableSeats;
}