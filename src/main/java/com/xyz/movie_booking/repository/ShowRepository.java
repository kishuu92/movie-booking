package com.xyz.movie_booking.repository;

import com.xyz.movie_booking.model.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("""
        SELECT s FROM Show s
        WHERE s.movie.id = :movieId
        AND s.theatre.city = :city
        AND s.showDate = :date
    """)
    List<Show> findShows(
            @Param("movieId") Long movieId,
            @Param("city") String city,
            @Param("date") LocalDate date
    );
}