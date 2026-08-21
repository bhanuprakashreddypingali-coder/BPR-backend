package com.bprflavorshub.bpr_flavors_hub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bprflavorshub.bpr_flavors_hub.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByUserIdAndFoodId(
            Long userId,
            Long foodId
    );

    void deleteByUserIdAndFoodId(
            Long userId,
            Long foodId
    );
}