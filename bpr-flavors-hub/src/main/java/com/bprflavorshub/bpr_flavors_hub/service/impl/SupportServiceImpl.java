package com.bprflavorshub.bpr_flavors_hub.service.impl;

import com.bprflavorshub.bpr_flavors_hub.dto.support.CreateSupportTicketRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageRequest;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportMessageResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.SupportTicketResponse;
import com.bprflavorshub.bpr_flavors_hub.dto.support.UpdateSupportTicketRequest;
import com.bprflavorshub.bpr_flavors_hub.entity.SupportMessage;
import com.bprflavorshub.bpr_flavors_hub.entity.SupportTicket;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.SupportMessageRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.SupportTicketRepository;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;
import com.bprflavorshub.bpr_flavors_hub.service.SupportService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SupportServiceImpl implements SupportService {

    private final SupportTicketRepository supportTicketRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final UserRepository userRepository;

    // =========================================================
    // CUSTOMER / OWNER
    // CREATE SUPPORT TICKET
    // =========================================================

    @Override
    public SupportTicketResponse createTicket(
            Long userId,
            CreateSupportTicketRequest request
    ) {

        User user = getUser(userId);

        validateTicketRequest(request);

        String priority =
                normalizePriority(request.getPriority());

        SupportTicket ticket = SupportTicket.builder()
                .user(user)
                .subject(request.getSubject().trim())
                .description(request.getDescription().trim())
                .category(request.getCategory().trim().toUpperCase())
                .priority(priority)
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SupportTicket savedTicket =
                supportTicketRepository.save(ticket);

        // =====================================================
        // FIRST MESSAGE
        // =====================================================

        SupportMessage firstMessage =
                SupportMessage.builder()
                        .ticket(savedTicket)
                        .sender(user)
                        .message(request.getDescription().trim())
                        .senderRole(getUserRole(user))
                        .createdAt(LocalDateTime.now())
                        .build();

        supportMessageRepository.save(firstMessage);

        return convertToResponse(savedTicket);
    }

    // =========================================================
    // CUSTOMER / OWNER
    // GET MY TICKETS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getMyTickets(
            Long userId
    ) {

        getUser(userId);

        return supportTicketRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // CUSTOMER / OWNER
    // GET SINGLE TICKET
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public SupportTicketResponse getMyTicket(
            Long userId,
            Long ticketId
    ) {

        User user = getUser(userId);

        SupportTicket ticket = getTicket(ticketId);

        if (ticket.getUser() == null ||
                !ticket.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to view this ticket"
            );
        }

        return convertToResponse(ticket);
    }

    // =========================================================
    // CUSTOMER / OWNER
    // ADD MESSAGE
    // =========================================================

    @Override
    public SupportMessageResponse addUserMessage(
            Long userId,
            Long ticketId,
            SupportMessageRequest request
    ) {

        User user = getUser(userId);

        SupportTicket ticket = getTicket(ticketId);

        if (ticket.getUser() == null ||
                !ticket.getUser().getId().equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to reply to this ticket"
            );
        }

        validateMessage(request);

        if ("CLOSED".equalsIgnoreCase(ticket.getStatus())) {

            throw new RuntimeException(
                    "This ticket is already closed"
            );
        }

        SupportMessage message =
                SupportMessage.builder()
                        .ticket(ticket)
                        .sender(user)
                        .message(request.getMessage().trim())
                        .senderRole(getUserRole(user))
                        .createdAt(LocalDateTime.now())
                        .build();

        SupportMessage savedMessage =
                supportMessageRepository.save(message);

        if ("OPEN".equalsIgnoreCase(ticket.getStatus())) {
            ticket.setStatus("IN_PROGRESS");
        }

        ticket.setUpdatedAt(LocalDateTime.now());

        supportTicketRepository.save(ticket);

        return convertMessageToResponse(savedMessage);
    }

    // =========================================================
    // ADMIN
    // GET ALL TICKETS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getAllTickets() {

        return supportTicketRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // ADMIN
    // GET TICKETS BY STATUS
    // =========================================================

    public List<SupportTicketResponse> getTicketsByStatus(
            String status
    ) {

        if (status == null ||
                status.trim().isEmpty()) {

            return getAllTickets();
        }

        String normalizedStatus =
                status.trim().toUpperCase();

        validateStatus(normalizedStatus);

        return supportTicketRepository
                .findByStatusOrderByCreatedAtDesc(
                        normalizedStatus
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // ADMIN
    // GET SINGLE TICKET
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public SupportTicketResponse getTicketById(
            Long ticketId
    ) {

        SupportTicket ticket =
                getTicket(ticketId);

        return convertToResponse(ticket);
    }

    // =========================================================
    // ADMIN
    // REPLY TO TICKET
    // =========================================================

    @Override
    public SupportMessageResponse addAdminMessage(
            Long adminId,
            Long ticketId,
            SupportMessageRequest request
    ) {

        User admin = getUser(adminId);

        SupportTicket ticket =
                getTicket(ticketId);

        validateMessage(request);

        if ("CLOSED".equalsIgnoreCase(ticket.getStatus())) {

            throw new RuntimeException(
                    "This ticket is already closed"
            );
        }

        SupportMessage message =
                SupportMessage.builder()
                        .ticket(ticket)
                        .sender(admin)
                        .message(request.getMessage().trim())
                        .senderRole("ADMIN")
                        .createdAt(LocalDateTime.now())
                        .build();

        SupportMessage savedMessage =
                supportMessageRepository.save(message);

        ticket.setStatus("IN_PROGRESS");
        ticket.setUpdatedAt(LocalDateTime.now());

        supportTicketRepository.save(ticket);

        return convertMessageToResponse(savedMessage);
    }

    // =========================================================
    // ADMIN
    // UPDATE TICKET
    // =========================================================

    public SupportTicketResponse updateTicket(
            Long ticketId,
            UpdateSupportTicketRequest request
    ) {

        if (request == null) {

            throw new RuntimeException(
                    "Update request cannot be null"
            );
        }

        SupportTicket ticket =
                getTicket(ticketId);

        // =====================================================
        // STATUS
        // =====================================================

        if (request.getStatus() != null &&
                !request.getStatus().trim().isEmpty()) {

            String status =
                    request.getStatus()
                            .trim()
                            .toUpperCase();

            validateStatus(status);

            ticket.setStatus(status);

            if ("RESOLVED".equals(status)) {

                ticket.setResolvedAt(
                        LocalDateTime.now()
                );
            }

            if ("CLOSED".equals(status)) {

                if (ticket.getResolvedAt() == null) {

                    ticket.setResolvedAt(
                            LocalDateTime.now()
                    );
                }
            }

            if ("OPEN".equals(status)) {

                ticket.setResolvedAt(null);
            }
        }

        // =====================================================
        // PRIORITY
        // =====================================================

        if (request.getPriority() != null &&
                !request.getPriority().trim().isEmpty()) {

            ticket.setPriority(
                    normalizePriority(
                            request.getPriority()
                    )
            );
        }

        // =====================================================
        // RESOLUTION
        // =====================================================

        if (request.getResolution() != null) {

            ticket.setResolution(
                    request.getResolution().trim()
            );
        }

        ticket.setUpdatedAt(LocalDateTime.now());

        SupportTicket savedTicket =
                supportTicketRepository.save(ticket);

        return convertToResponse(savedTicket);
    }

    // =========================================================
    // ADMIN
    // CLOSE TICKET
    // =========================================================

    @Override
    public SupportTicketResponse closeTicket(
            Long ticketId
    ) {

        SupportTicket ticket =
                getTicket(ticketId);

        ticket.setStatus("CLOSED");

        if (ticket.getResolvedAt() == null) {

            ticket.setResolvedAt(
                    LocalDateTime.now()
            );
        }

        ticket.setUpdatedAt(
                LocalDateTime.now()
        );

        SupportTicket savedTicket =
                supportTicketRepository.save(ticket);

        return convertToResponse(savedTicket);
    }

    // =========================================================
    // ADMIN
    // REOPEN TICKET
    // =========================================================

    @Override
    public SupportTicketResponse reopenTicket(
            Long ticketId
    ) {

        SupportTicket ticket =
                getTicket(ticketId);

        ticket.setStatus("OPEN");
        ticket.setResolvedAt(null);
        ticket.setUpdatedAt(
                LocalDateTime.now()
        );

        SupportTicket savedTicket =
                supportTicketRepository.save(ticket);

        return convertToResponse(savedTicket);
    }

    // =========================================================
    // ADMIN
    // DELETE TICKET
    // =========================================================

    @Override
    public void deleteTicket(
            Long ticketId
    ) {

        SupportTicket ticket =
                getTicket(ticketId);

        /*
         * Delete messages first so that the operation
         * also works when the database has a foreign-key
         * relationship from support_messages to support_tickets.
         */

        List<SupportMessage> messages =
                supportMessageRepository
                        .findByTicketIdOrderByCreatedAtAsc(
                                ticketId
                        );

        if (!messages.isEmpty()) {

            supportMessageRepository
                    .deleteAll(messages);
        }

        supportTicketRepository.delete(ticket);
    }

    // =========================================================
    // GET USER
    // =========================================================

    private User getUser(
            Long userId
    ) {

        if (userId == null) {

            throw new RuntimeException(
                    "User ID cannot be null"
            );
        }

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    // =========================================================
    // GET TICKET
    // =========================================================

    private SupportTicket getTicket(
            Long ticketId
    ) {

        if (ticketId == null) {

            throw new RuntimeException(
                    "Ticket ID cannot be null"
            );
        }

        return supportTicketRepository
                .findById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Support ticket not found"
                        )
                );
    }

    // =========================================================
    // VALIDATE TICKET REQUEST
    // =========================================================

    private void validateTicketRequest(
            CreateSupportTicketRequest request
    ) {

        if (request == null) {

            throw new RuntimeException(
                    "Ticket request cannot be null"
            );
        }

        if (request.getSubject() == null ||
                request.getSubject().trim().isEmpty()) {

            throw new RuntimeException(
                    "Subject is required"
            );
        }

        if (request.getDescription() == null ||
                request.getDescription().trim().isEmpty()) {

            throw new RuntimeException(
                    "Description is required"
            );
        }

        if (request.getCategory() == null ||
                request.getCategory().trim().isEmpty()) {

            throw new RuntimeException(
                    "Category is required"
            );
        }
    }

    // =========================================================
    // VALIDATE MESSAGE
    // =========================================================

    private void validateMessage(
            SupportMessageRequest request
    ) {

        if (request == null) {

            throw new RuntimeException(
                    "Message request cannot be null"
            );
        }

        if (request.getMessage() == null ||
                request.getMessage().trim().isEmpty()) {

            throw new RuntimeException(
                    "Message cannot be empty"
            );
        }
    }

    // =========================================================
    // VALIDATE STATUS
    // =========================================================

    private void validateStatus(
            String status
    ) {

        if (!List.of(
                "OPEN",
                "IN_PROGRESS",
                "RESOLVED",
                "CLOSED"
        ).contains(status)) {

            throw new RuntimeException(
                    "Invalid ticket status: " + status
            );
        }
    }

    // =========================================================
    // NORMALIZE PRIORITY
    // =========================================================

    private String normalizePriority(
            String priority
    ) {

        if (priority == null ||
                priority.trim().isEmpty()) {

            return "MEDIUM";
        }

        String value =
                priority.trim().toUpperCase();

        if (!List.of(
                "LOW",
                "MEDIUM",
                "HIGH",
                "URGENT"
        ).contains(value)) {

            throw new RuntimeException(
                    "Invalid ticket priority: " + value
            );
        }

        return value;
    }

    // =========================================================
    // GET USER ROLE
    // =========================================================

    private String getUserRole(
            User user
    ) {

        if (user == null ||
                user.getRole() == null) {

            return "USER";
        }

        return user.getRole()
                .toString()
                .replace("ROLE_", "")
                .toUpperCase();
    }

    // =========================================================
    // TICKET → RESPONSE
    // =========================================================

    private SupportTicketResponse convertToResponse(
            SupportTicket ticket
    ) {

        User user = ticket.getUser();

        List<SupportMessageResponse> messages =
                supportMessageRepository
                        .findByTicketIdOrderByCreatedAtAsc(
                                ticket.getId()
                        )
                        .stream()
                        .map(this::convertMessageToResponse)
                        .toList();

        return SupportTicketResponse.builder()
                .id(ticket.getId())
                .userId(user.getId())
                .userName(user.getFullName())
                .userEmail(user.getEmail())
                .userRole(getUserRole(user))
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .resolution(ticket.getResolution())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .messages(messages)
                .build();
    }

    // =========================================================
    // MESSAGE → RESPONSE
    // =========================================================

    private SupportMessageResponse convertMessageToResponse(
            SupportMessage message
    ) {

        User sender = message.getSender();

        return SupportMessageResponse.builder()
                .id(message.getId())
                .ticketId(message.getTicket().getId())
                .senderId(sender.getId())
                .senderName(sender.getFullName())
                .senderRole(message.getSenderRole())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .build();
    }
}