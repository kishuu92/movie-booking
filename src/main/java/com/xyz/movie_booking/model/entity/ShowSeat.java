package com.xyz.movie_booking.model.entity;

import com.xyz.movie_booking.model.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "show_seat",
        indexes = {
                @Index(name = "idx_show", columnList = "show_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"show_id", "seat_number"})
        }
)
@NoArgsConstructor
@Getter
@Setter
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    private String seatNumber; // A1, A2, B1...

    @Enumerated(EnumType.STRING)
    private SeatStatus status;
}