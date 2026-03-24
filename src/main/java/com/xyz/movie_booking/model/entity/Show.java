package com.xyz.movie_booking.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "shows",
        indexes = {
                @Index(name = "idx_movie_date", columnList = "movie_id, show_date"),
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;

    @Column(nullable = false)
    private LocalDate showDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    @Min(0)
    private Integer totalSeats;

    @Column(nullable = false)
    @Min(0)
    private Integer availableSeats;
}