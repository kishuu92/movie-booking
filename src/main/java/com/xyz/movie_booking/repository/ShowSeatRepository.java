package com.xyz.movie_booking.repository;

import com.xyz.movie_booking.model.entity.ShowSeat;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShowId(Long showId);

    // Locks selected seats for update to prevent concurrent booking (SELECT FOR UPDATE)
    // Lock timeout ensures fail-fast behavior under high contention
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT s FROM ShowSeat s
                WHERE s.show.id = :showId
                AND s.seatNumber IN :seatNumbers
                ORDER BY s.seatNumber
            """)
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    List<ShowSeat> findSeatsForUpdate(
            @Param("showId") Long showId,
            @Param("seatNumbers") List<String> seatNumbers
    );
}