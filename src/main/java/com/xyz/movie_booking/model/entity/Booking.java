package com.xyz.movie_booking.model.entity;

import com.xyz.movie_booking.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "booking",
        indexes = {
                @Index(name = "idx_user", columnList = "user_id"),
                @Index(name = "idx_show_booking", columnList = "show_id")
        }
)
@NoArgsConstructor
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // passed from request (no auth implemented)

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    private Integer numberOfSeats;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;
}