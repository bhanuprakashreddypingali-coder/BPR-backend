package com.bprflavorshub.bpr_flavors_hub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String foodName;

    @Column(length = 1000)
    private String description;

    private Double price;

    private String image;

    private String category;

    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler",
        "owner"
    })
    private Restaurant restaurant;
}