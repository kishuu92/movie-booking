package com.xyz.movie_booking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "booking_seat",
        indexes = {
                @Index(name = "idx_booking", columnList = "booking_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"booking_id", "seat_number"})
        }
)
@NoArgsConstructor
@Getter
@Setter
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Stored in normalized format (uppercase, trimmed)
    @Column(nullable = false, length = 10)
    private String seatNumber;
}