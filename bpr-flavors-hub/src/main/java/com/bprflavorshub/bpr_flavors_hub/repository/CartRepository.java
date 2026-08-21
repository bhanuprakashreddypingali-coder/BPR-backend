package com.bprflavorshub.bpr_flavors_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bprflavorshub.bpr_flavors_hub.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserId(Long userId);

    Optional<Cart> findByUserIdAndFoodId(Long userId, Long foodId);
}