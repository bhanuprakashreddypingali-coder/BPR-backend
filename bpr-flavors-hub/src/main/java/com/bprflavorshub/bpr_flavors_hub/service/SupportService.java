package com.bprflavorshub.bpr_flavors_hub.service;

import com.bprflavorshub.bpr_flavors_hub.dto.support.CreateSupportTicketRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportTicketResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.UpdateSupportTicketRequest;

import java.util.List;

public interface SupportService {

    SupportTicketResponse createTicket(
            Long userId,
            CreateSupportTicketRequest request
    );

    List<SupportTicketResponse> getMyTickets(
            Long userId
    );

    SupportTicketResponse getMyTicket(
            Long userId,
            Long ticketId
    );

    SupportMessageResponse addUserMessage(
            Long userId,
            Long ticketId,
            SupportMessageRequest request
    );

    List<SupportTicketResponse> getAllTickets();

    List<SupportTicketResponse> getTicketsByStatus(
            String status
    );

    SupportTicketResponse getAdminTicket(
            Long ticketId
    );

    SupportMessageResponse addAdminMessage(
            Long adminId,
            Long ticketId,
            SupportMessageRequest request
    );

    SupportTicketResponse updateTicket(
            Long ticketId,
            UpdateSupportTicketRequest request
    );
}