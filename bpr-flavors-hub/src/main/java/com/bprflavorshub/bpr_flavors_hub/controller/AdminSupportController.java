package com.bprflavorshub.bpr_flavors_hub.controller;

import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportTicketResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.UpdateSupportTicketRequest;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.SupportService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/support")
@RequiredArgsConstructor
public class AdminSupportController {

    private final SupportService supportService;
    private final UserRepository userRepository;

    // =========================================================
    // GET ALL SUPPORT TICKETS
    // =========================================================

    @GetMapping("/tickets")
    public ResponseEntity<List<SupportTicketResponse>> getAllTickets(
            @RequestParam(required = false) String status
    ) {

        if (status == null || status.trim().isEmpty()) {

            return ResponseEntity.ok(
                    supportService.getAllTickets()
            );
        }

        return ResponseEntity.ok(
                supportService.getTicketsByStatus(status)
        );
    }

    // =========================================================
    // GET SINGLE SUPPORT TICKET
    // =========================================================

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<SupportTicketResponse> getTicket(
            @PathVariable Long ticketId
    ) {

        return ResponseEntity.ok(
                supportService.getAdminTicket(ticketId)
        );
    }

    // =========================================================
    // ADMIN REPLY TO TICKET
    // =========================================================

    @PostMapping("/tickets/{ticketId}/messages")
    public ResponseEntity<SupportMessageResponse> addMessage(
            Authentication authentication,
            @PathVariable Long ticketId,
            @RequestBody SupportMessageRequest request
    ) {

        Long adminId =
                getAuthenticatedAdminId(authentication);

        return ResponseEntity.ok(
                supportService.addAdminMessage(
                        adminId,
                        ticketId,
                        request
                )
        );
    }

    // =========================================================
    // ADMIN UPDATE / RESOLVE / CLOSE
    // =========================================================

    @PutMapping("/tickets/{ticketId}")
    public ResponseEntity<SupportTicketResponse> updateTicket(
            @PathVariable Long ticketId,
            @RequestBody UpdateSupportTicketRequest request
    ) {

        return ResponseEntity.ok(
                supportService.updateTicket(
                        ticketId,
                        request
                )
        );
    }

    // =========================================================
    // GET AUTHENTICATED ADMIN ID
    // =========================================================

    private Long getAuthenticatedAdminId(
            Authentication authentication
    ) {

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Admin is not authenticated"
            );
        }

        /*
         * IMPORTANT:
         *
         * Your JWT uses PHONE as the authenticated username.
         *
         * Example:
         *
         * AUTH USER : 9391902028
         *
         * So we must find the admin by PHONE,
         * not by EMAIL.
         */

        String phone =
                authentication.getName().trim();

        User admin =
                userRepository
                        .findByPhone(phone)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Admin user not found"
                                )
                        );

        // Extra safety check
        if (admin.getRole() == null ||
                !admin.getRole()
                        .toString()
                        .replace("ROLE_", "")
                        .equalsIgnoreCase("ADMIN")) {

            throw new RuntimeException(
                    "Authenticated user is not an admin"
            );
        }

        return admin.getId();
    }
}