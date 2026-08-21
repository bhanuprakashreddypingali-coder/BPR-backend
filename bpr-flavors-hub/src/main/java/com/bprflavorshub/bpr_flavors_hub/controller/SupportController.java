package com.bprflavorshub.bpr_flavors_hub.controller;

import com.bprflavorshub.bpr_flavors_hub.dto.support.CreateSupportTicketRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportTicketResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.SupportService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final UserRepository userRepository;

    // =========================================================
    // CREATE SUPPORT TICKET
    // =========================================================

    @PostMapping("/tickets")
    public ResponseEntity<SupportTicketResponse> createTicket(
            Authentication authentication,
            @RequestBody CreateSupportTicketRequest request
    ) {

        Long userId = getAuthenticatedUserId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        supportService.createTicket(
                                userId,
                                request
                        )
                );
    }

    // =========================================================
    // GET MY TICKETS
    // =========================================================

    @GetMapping("/tickets")
    public ResponseEntity<List<SupportTicketResponse>> getMyTickets(
            Authentication authentication
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                supportService.getMyTickets(userId)
        );
    }

    // =========================================================
    // GET MY SINGLE TICKET
    // =========================================================

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<SupportTicketResponse> getMyTicket(
            Authentication authentication,
            @PathVariable Long ticketId
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                supportService.getMyTicket(
                        userId,
                        ticketId
                )
        );
    }

    // =========================================================
    // CUSTOMER / OWNER REPLY
    // =========================================================

    @PostMapping("/tickets/{ticketId}/messages")
    public ResponseEntity<SupportMessageResponse> addMessage(
            Authentication authentication,
            @PathVariable Long ticketId,
            @RequestBody SupportMessageRequest request
    ) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                supportService.addUserMessage(
                        userId,
                        ticketId,
                        request
                )
        );
    }

    // =========================================================
    // GET AUTHENTICATED USER ID
    // =========================================================

    private Long getAuthenticatedUserId(
            Authentication authentication
    ) {

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String loginIdentifier =
                authentication.getName().trim();

        /*
         * Your current JWT authentication uses PHONE
         * as the authenticated username.
         *
         * Example:
         *
         * AUTH USER : 1122334455
         *
         * Therefore we must search by PHONE,
         * not EMAIL.
         */

        User user =
                userRepository
                        .findByPhone(loginIdentifier)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Authenticated user not found"
                                )
                        );

        return user.getId();
    }
}