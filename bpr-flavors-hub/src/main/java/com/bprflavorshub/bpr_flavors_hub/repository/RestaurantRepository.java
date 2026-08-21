package com.bprflavorshub.bpr_flavors_hub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bprflavorshub.bpr_flavors_hub.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByOwnerId(Long ownerId);

}