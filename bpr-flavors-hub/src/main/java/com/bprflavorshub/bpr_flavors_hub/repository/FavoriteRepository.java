package com.bprflavorshub.bpr_flavors_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bprflavorshub.bpr_flavors_hub.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserIdAndFoodId(Long userId, Long foodId);

}