package com.xyz.movie_booking.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "theatre",
        indexes = {
                @Index(name = "idx_city", columnList = "city")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "city"})
        }
)
@NoArgsConstructor
@Getter
@Setter
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // Should be stored in normalized form (lowercase, trimmed)
    @Column(nullable = false, length = 50)
    private String city;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String contact;
}