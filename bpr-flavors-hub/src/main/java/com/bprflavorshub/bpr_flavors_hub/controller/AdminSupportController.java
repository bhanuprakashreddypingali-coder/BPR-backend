package com.bprflavorshub.bpr_flavors_hub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportTicketResponse;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.SupportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/support")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSupportController {

    private final SupportService supportService;
    private final UserRepository userRepository;

    // =========================================================
    // GET ALL TICKETS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<SupportTicketResponse>> getAllTickets() {

        return ResponseEntity.ok(
                supportService.getAllTickets()
        );
    }

    // =========================================================
    // GET TICKETS BY STATUS
    // =========================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SupportTicketResponse>> getTicketsByStatus(
            @PathVariable String status
    ) {

        return ResponseEntity.ok(
                supportService.getTicketsByStatus(status)
        );
    }

    // =========================================================
    // GET SINGLE TICKET
    // =========================================================

    @GetMapping("/{ticketId}")
    public ResponseEntity<SupportTicketResponse> getTicket(
            @PathVariable Long ticketId
    ) {

        return ResponseEntity.ok(
                supportService.getTicketById(ticketId)
        );
    }

    // =========================================================
    // ADMIN REPLY
    // =========================================================

    @PostMapping("/{ticketId}/messages")
    public ResponseEntity<SupportMessageResponse> replyTicket(
            Authentication authentication,
            @PathVariable Long ticketId,
            @RequestBody SupportMessageRequest request
    ) {

        Long adminId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                supportService.addAdminMessage(
                        adminId,
                        ticketId,
                        request
                )
        );
    }

    // =========================================================
    // CLOSE
    // =========================================================

    @PutMapping("/{ticketId}/close")
    public ResponseEntity<SupportTicketResponse> closeTicket(
            @PathVariable Long ticketId
    ) {

        return ResponseEntity.ok(
                supportService.closeTicket(ticketId)
        );
    }

    // =========================================================
    // REOPEN
    // =========================================================

    @PutMapping("/{ticketId}/reopen")
    public ResponseEntity<SupportTicketResponse> reopenTicket(
            @PathVariable Long ticketId
    ) {

        return ResponseEntity.ok(
                supportService.reopenTicket(ticketId)
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<String> deleteTicket(
            @PathVariable Long ticketId
    ) {

        supportService.deleteTicket(ticketId);

        return ResponseEntity.ok(
                "Support ticket deleted successfully."
        );
    }

    // =========================================================
    // GET AUTHENTICATED ADMIN ID
    // =========================================================

    private Long getAuthenticatedUserId(
            Authentication authentication
    ) {

        if (authentication == null ||
                authentication.getName() == null ||
                authentication.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Admin is not authenticated"
            );
        }

        String phone =
                authentication.getName().trim();

        User user =
                userRepository
                        .findByPhone(phone)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Authenticated admin not found"
                                )
                        );

        return user.getId();
    }
}