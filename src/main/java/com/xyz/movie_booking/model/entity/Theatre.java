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
        }
)
@NoArgsConstructor
@Getter
@Setter
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    private String address;
    private String contact;
}
