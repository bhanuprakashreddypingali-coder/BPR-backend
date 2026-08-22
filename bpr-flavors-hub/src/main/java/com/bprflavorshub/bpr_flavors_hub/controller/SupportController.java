package com.bprflavorshub.bpr_flavors_hub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.support.CreateSupportTicketRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportTicketResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.UpdateSupportTicketRequest;
import com.bprflavorshub.bpr_flavors_hub.service.SupportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
public class SupportController {

    private final SupportService supportService;

    // =========================================================
    // CUSTOMER / OWNER
    // CREATE TICKET
    // POST /api/support/tickets
    // =========================================================

    @PostMapping("/tickets")
    public ResponseEntity<SupportTicketResponse> createTicket(
            Principal principal,
            @RequestBody CreateSupportTicketRequest request
    ) {

        return ResponseEntity.ok(
                supportService.createTicket(
                        principal.getName(),
                        request
                )
        );
    }

    // =========================================================
    // CUSTOMER / OWNER
    // GET MY TICKETS
    // GET /api/support/tickets
    // =========================================================

    @GetMapping("/tickets")
    public ResponseEntity<List<SupportTicketResponse>> getMyTickets(
            Principal principal
    ) {

        return ResponseEntity.ok(
                supportService.getMyTickets(
                        principal.getName()
                )
        );
    }

    // =========================================================
    // CUSTOMER / OWNER
    // GET SINGLE TICKET
    // GET /api/support/tickets/{ticketId}
    // =========================================================

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<SupportTicketResponse> getMyTicket(
            Principal principal,
            @PathVariable Long ticketId
    ) {

        return ResponseEntity.ok(
                supportService.getMyTicket(
                        principal.getName(),
                        ticketId
                )
        );
    }

    // =========================================================
    // CUSTOMER / OWNER
    // REPLY
    // POST /api/support/tickets/{ticketId}/messages
    // =========================================================

    @PostMapping("/tickets/{ticketId}/messages")
    public ResponseEntity<SupportMessageResponse> addUserMessage(
            Principal principal,
            @PathVariable Long ticketId,
            @RequestBody SupportMessageRequest request
    ) {

        return ResponseEntity.ok(
                supportService.addUserMessage(
                        principal.getName(),
                        ticketId,
                        request
                )
        );
    }

    // =========================================================
    // ADMIN - GET ALL
    // GET /api/support/admin
    // =========================================================

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupportTicketResponse>> getAllTickets(
            @RequestParam(required = false) String status
    ) {

        if (status == null ||
                status.trim().isEmpty()) {

            return ResponseEntity.ok(
                    supportService.getAllTickets()
            );
        }

        return ResponseEntity.ok(
                supportService.getTicketsByStatus(status)
        );
    }

    // =========================================================
    // ADMIN - GET ONE
    // GET /api/support/admin/{ticketId}
    // =========================================================

    @GetMapping("/admin/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> getAdminTicket(
            @PathVariable Long ticketId
    ) {

        return ResponseEntity.ok(
                supportService.getTicketById(
                        ticketId
                )
        );
    }

    // =========================================================
    // ADMIN - REPLY
    // POST /api/support/admin/{ticketId}/messages
    // =========================================================

    @PostMapping("/admin/{ticketId}/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupportMessageResponse> addAdminMessage(
            Principal principal,
            @PathVariable Long ticketId,
            @RequestBody SupportMessageRequest request
    ) {

        return ResponseEntity.ok(
                supportService.addAdminMessage(
                        principal.getName(),
                        ticketId,
                        request
                )
        );
    }

    // =========================================================
    // ADMIN - UPDATE
    // PUT /api/support/admin/{ticketId}
    // =========================================================

    @PutMapping("/admin/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
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
    // ADMIN - CLOSE
    // PUT /api/support/admin/{ticketId}/close
    // =========================================================

    @PutMapping("/admin/{ticketId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> closeTicket(
            @PathVariable Long ticketId
    ) {

        return ResponseEntity.ok(
                supportService.closeTicket(
                        ticketId
                )
        );
    }

    // =========================================================
    // ADMIN - REOPEN
    // PUT /api/support/admin/{ticketId}/reopen
    // =========================================================

    @PutMapping("/admin/{ticketId}/reopen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> reopenTicket(
            @PathVariable Long ticketId
    ) {

        return ResponseEntity.ok(
                supportService.reopenTicket(
                        ticketId
                )
        );
    }

    // =========================================================
    // ADMIN - DELETE
    // DELETE /api/support/admin/{ticketId}
    // =========================================================

    @DeleteMapping("/admin/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long ticketId
    ) {

        supportService.deleteTicket(
                ticketId
        );

        return ResponseEntity.noContent().build();
    }
}