package com.bprflavorshub.bpr_flavors_hub.service;

import java.util.List;

import com.bprflavorshub.bpr_flavors_hub.dto.support.CreateSupportTicketRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportTicketResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.UpdateSupportTicketRequest;

public interface SupportService {

    // =========================================================
    // CUSTOMER / OWNER
    // =========================================================

    SupportTicketResponse createTicket(
            String username,
            CreateSupportTicketRequest request
    );

    List<SupportTicketResponse> getMyTickets(
            String username
    );

    SupportTicketResponse getMyTicket(
            String username,
            Long ticketId
    );

    SupportMessageResponse addUserMessage(
            String username,
            Long ticketId,
            SupportMessageRequest request
    );

    // =========================================================
    // ADMIN
    // =========================================================

    List<SupportTicketResponse> getAllTickets();

    List<SupportTicketResponse> getTicketsByStatus(
            String status
    );

    SupportTicketResponse getTicketById(
            Long ticketId
    );

    SupportMessageResponse addAdminMessage(
            String adminUsername,
            Long ticketId,
            SupportMessageRequest request
    );

    SupportTicketResponse updateTicket(
            Long ticketId,
            UpdateSupportTicketRequest request
    );

    SupportTicketResponse closeTicket(
            Long ticketId
    );

    SupportTicketResponse reopenTicket(
            Long ticketId
    );

    void deleteTicket(
            Long ticketId
    );
}