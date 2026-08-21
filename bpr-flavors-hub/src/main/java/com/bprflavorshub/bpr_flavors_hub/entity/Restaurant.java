package com.bprflavorshub.bpr_flavors_hub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String restaurantName;

    private String ownerName;

    private String email;

    private String phone;

    private String address;

    private String image;

    @Column(length = 1000)
    private String description;

    private String openingTime;

    private String closingTime;

    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
    })
    private User owner;
}