package com.bprflavorshub.bpr_flavors_hub.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportTicketResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.UpdateSupportTicketRequest;
import com.bprflavorshub.bpr_flavors_hub.service.SupportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/support")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSupportController {

    private final SupportService supportService;

    // =========================================================
    // GET ALL TICKETS
    // GET /api/admin/support
    // =========================================================

    @GetMapping
    public ResponseEntity<List<SupportTicketResponse>> getAllTickets() {

        return ResponseEntity.ok(
                supportService.getAllTickets()
        );
    }

    // =========================================================
    // GET TICKETS BY STATUS
    // GET /api/admin/support?status=OPEN
    // =========================================================

    @GetMapping(params = "status")
    public ResponseEntity<List<SupportTicketResponse>> getTicketsByStatus(
            @RequestParam String status
    ) {

        return ResponseEntity.ok(
                supportService.getTicketsByStatus(status)
        );
    }

    // =========================================================
    // GET SINGLE TICKET
    // GET /api/admin/support/{ticketId}
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
    // POST /api/admin/support/{ticketId}/messages
    // =========================================================

    @PostMapping("/{ticketId}/messages")
    public ResponseEntity<SupportMessageResponse> replyTicket(
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
    // UPDATE TICKET
    // PUT /api/admin/support/{ticketId}
    // =========================================================

    @PutMapping("/{ticketId}")
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
    // CLOSE
    // PUT /api/admin/support/{ticketId}/close
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
    // PUT /api/admin/support/{ticketId}/reopen
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
    // DELETE /api/admin/support/{ticketId}
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
}