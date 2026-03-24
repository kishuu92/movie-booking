package com.xyz.movie_booking.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "movies",
        indexes = {
                @Index(name = "idx_movie_name", columnList = "name")
        }
)
@NoArgsConstructor
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Movie title
    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    @Min(1)
    private Integer durationInMinutes;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(nullable = false, length = 50)
    private String genre;
}