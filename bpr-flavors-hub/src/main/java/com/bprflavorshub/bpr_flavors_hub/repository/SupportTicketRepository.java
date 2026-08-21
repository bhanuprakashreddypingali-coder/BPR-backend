package com.bprflavorshub.bpr_flavors_hub.repository;

import com.bprflavorshub.bpr_flavors_hub.entity.SupportTicket;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository
        extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByUserOrderByCreatedAtDesc(User user);

    List<SupportTicket> findAllByOrderByCreatedAtDesc();

    List<SupportTicket> findByStatusOrderByCreatedAtDesc(String status);

    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId);
}