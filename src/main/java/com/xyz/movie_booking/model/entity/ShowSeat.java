package com.xyz.movie_booking.model.entity;

import com.xyz.movie_booking.model.enums.SeatStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "show_seat",
        indexes = {
                @Index(name = "idx_show_seat", columnList = "show_id, seat_number")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    // Stored in normalized format (uppercase, trimmed)
    @Column(nullable = false, length = 10)
    private String seatNumber; // A1, A2, B1...

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Column(nullable = false)
    @Min(0)
    private Double price;
}