package com.bprflavorshub.bpr_flavors_hub.repository;

import com.bprflavorshub.bpr_flavors_hub.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);

    List<Address> findByUserIdOrderByIsDefaultDescIdDesc(Long userId);
}