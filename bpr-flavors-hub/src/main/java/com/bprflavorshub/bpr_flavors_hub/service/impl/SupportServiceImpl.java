package com.bprflavorshub.bpr_flavors_hub.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@RequiredArgsConstructor
@Transactional
public class SupportServiceImpl implements SupportService {

    private final SupportTicketRepository supportTicketRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final UserRepository userRepository;

    // =========================================================
    // CREATE TICKET
    // =========================================================

    @Override
    public SupportTicketResponse createTicket(
            String username,
            CreateSupportTicketRequest request
    ) {

        User user = getUserByPrincipal(username);

        validateTicketRequest(request);

        LocalDateTime now = LocalDateTime.now();

        SupportTicket ticket = SupportTicket.builder()
                .user(user)
                .subject(request.getSubject().trim())
                .description(request.getDescription().trim())
                .category(normalizeCategory(request.getCategory()))
                .priority(normalizePriority(request.getPriority()))
                .status("OPEN")
                .createdAt(now)
                .updatedAt(now)
                .build();

        SupportTicket savedTicket =
                supportTicketRepository.save(ticket);

        SupportMessage firstMessage =
                SupportMessage.builder()
                        .ticket(savedTicket)
                        .sender(user)
                        .message(request.getDescription().trim())
                        .senderRole(getUserRole(user))
                        .createdAt(now)
                        .build();

        supportMessageRepository.save(firstMessage);

        return convertToResponse(savedTicket);
    }

    // =========================================================
    // GET MY TICKETS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getMyTickets(
            String username
    ) {

        User user = getUserByPrincipal(username);

        return supportTicketRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET MY SINGLE TICKET
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public SupportTicketResponse getMyTicket(
            String username,
            Long ticketId
    ) {

        User user = getUserByPrincipal(username);

        SupportTicket ticket = getTicket(ticketId);

        validateOwner(ticket, user);

        return convertToResponse(ticket);
    }

    // =========================================================
    // USER / OWNER MESSAGE
    // =========================================================

    @Override
    public SupportMessageResponse addUserMessage(
            String username,
            Long ticketId,
            SupportMessageRequest request
    ) {

        User user = getUserByPrincipal(username);

        SupportTicket ticket = getTicket(ticketId);

        validateOwner(ticket, user);

        validateMessage(request);

        if ("CLOSED".equalsIgnoreCase(ticket.getStatus())) {

            throw new RuntimeException(
                    "This ticket is closed and cannot receive replies."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        SupportMessage message =
                SupportMessage.builder()
                        .ticket(ticket)
                        .sender(user)
                        .message(request.getMessage().trim())
                        .senderRole(getUserRole(user))
                        .createdAt(now)
                        .build();

        SupportMessage savedMessage =
                supportMessageRepository.save(message);

        if ("OPEN".equalsIgnoreCase(ticket.getStatus())) {
            ticket.setStatus("IN_PROGRESS");
        }

        ticket.setUpdatedAt(now);

        supportTicketRepository.save(ticket);

        return convertMessageToResponse(savedMessage);
    }

    // =========================================================
    // ADMIN - ALL TICKETS
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
    // ADMIN - BY STATUS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getTicketsByStatus(
            String status
    ) {

        if (status == null ||
                status.trim().isEmpty()) {

            return getAllTickets();
        }

        String normalized =
                status.trim().toUpperCase();

        validateStatus(normalized);

        return supportTicketRepository
                .findByStatusOrderByCreatedAtDesc(normalized)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // ADMIN - SINGLE TICKET
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public SupportTicketResponse getTicketById(
            Long ticketId
    ) {

        return convertToResponse(
                getTicket(ticketId)
        );
    }

    // =========================================================
    // ADMIN - MESSAGE
    // =========================================================

    @Override
    public SupportMessageResponse addAdminMessage(
            String adminUsername,
            Long ticketId,
            SupportMessageRequest request
    ) {

        User admin =
                getUserByPrincipal(adminUsername);

        SupportTicket ticket =
                getTicket(ticketId);

        validateMessage(request);

        if ("CLOSED".equalsIgnoreCase(ticket.getStatus())) {

            throw new RuntimeException(
                    "This ticket is closed."
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        SupportMessage message =
                SupportMessage.builder()
                        .ticket(ticket)
                        .sender(admin)
                        .message(request.getMessage().trim())
                        .senderRole("ADMIN")
                        .createdAt(now)
                        .build();

        SupportMessage saved =
                supportMessageRepository.save(message);

        ticket.setStatus("IN_PROGRESS");
        ticket.setUpdatedAt(now);

        supportTicketRepository.save(ticket);

        return convertMessageToResponse(saved);
    }

    // =========================================================
    // ADMIN - UPDATE
    // =========================================================

    @Override
    public SupportTicketResponse updateTicket(
            Long ticketId,
            UpdateSupportTicketRequest request
    ) {

        if (request == null) {

            throw new RuntimeException(
                    "Update request cannot be null."
            );
        }

        SupportTicket ticket =
                getTicket(ticketId);

        if (request.getStatus() != null &&
                !request.getStatus().trim().isEmpty()) {

            String status =
                    request.getStatus()
                            .trim()
                            .toUpperCase();

            validateStatus(status);

            ticket.setStatus(status);

            if ("RESOLVED".equals(status) ||
                    "CLOSED".equals(status)) {

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

        if (request.getPriority() != null &&
                !request.getPriority().trim().isEmpty()) {

            ticket.setPriority(
                    normalizePriority(
                            request.getPriority()
                    )
            );
        }

        if (request.getResolution() != null) {

            ticket.setResolution(
                    request.getResolution().trim()
            );
        }

        ticket.setUpdatedAt(
                LocalDateTime.now()
        );

        return convertToResponse(
                supportTicketRepository.save(ticket)
        );
    }

    // =========================================================
    // CLOSE
    // =========================================================

    @Override
    public SupportTicketResponse closeTicket(
            Long ticketId
    ) {

        SupportTicket ticket =
                getTicket(ticketId);

        LocalDateTime now =
                LocalDateTime.now();

        ticket.setStatus("CLOSED");

        if (ticket.getResolvedAt() == null) {

            ticket.setResolvedAt(now);
        }

        ticket.setUpdatedAt(now);

        return convertToResponse(
                supportTicketRepository.save(ticket)
        );
    }

    // =========================================================
    // REOPEN
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

        return convertToResponse(
                supportTicketRepository.save(ticket)
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteTicket(
            Long ticketId
    ) {

        SupportTicket ticket =
                getTicket(ticketId);

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
    // FIND USER
    // =========================================================

    private User getUserByPrincipal(
            String username
    ) {

        if (username == null ||
                username.trim().isEmpty()) {

            throw new RuntimeException(
                    "Authenticated user not found."
            );
        }

        /*
         * Your application uses phone number for login,
         * so first try username/phone.
         */

        return userRepository
                .findByPhone(username)
                .orElseGet(() ->
                        userRepository
                                .findByEmail(username)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "User not found: "
                                                        + username
                                        )
                                )
                );
    }

    // =========================================================
    // FIND TICKET
    // =========================================================

    private SupportTicket getTicket(
            Long ticketId
    ) {

        if (ticketId == null) {

            throw new RuntimeException(
                    "Ticket ID cannot be null."
            );
        }

        return supportTicketRepository
                .findById(ticketId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Support ticket not found."
                        )
                );
    }

    // =========================================================
    // OWNER VALIDATION
    // =========================================================

    private void validateOwner(
            SupportTicket ticket,
            User user
    ) {

        if (ticket.getUser() == null ||
                user == null ||
                !ticket.getUser()
                        .getId()
                        .equals(user.getId())) {

            throw new RuntimeException(
                    "You are not allowed to access this ticket."
            );
        }
    }

    // =========================================================
    // VALIDATE CREATE REQUEST
    // =========================================================

    private void validateTicketRequest(
            CreateSupportTicketRequest request
    ) {

        if (request == null) {

            throw new RuntimeException(
                    "Ticket request cannot be null."
            );
        }

        if (request.getSubject() == null ||
                request.getSubject().trim().isEmpty()) {

            throw new RuntimeException(
                    "Subject is required."
            );
        }

        if (request.getDescription() == null ||
                request.getDescription().trim().isEmpty()) {

            throw new RuntimeException(
                    "Description is required."
            );
        }

        if (request.getCategory() == null ||
                request.getCategory().trim().isEmpty()) {

            throw new RuntimeException(
                    "Category is required."
            );
        }
    }

    // =========================================================
    // VALIDATE MESSAGE
    // =========================================================

    private void validateMessage(
            SupportMessageRequest request
    ) {

        if (request == null ||
                request.getMessage() == null ||
                request.getMessage().trim().isEmpty()) {

            throw new RuntimeException(
                    "Message cannot be empty."
            );
        }
    }

    // =========================================================
    // STATUS
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
    // PRIORITY
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
                    "Invalid priority: " + value
            );
        }

        return value;
    }

    // =========================================================
    // CATEGORY
    // =========================================================

    private String normalizeCategory(
            String category
    ) {

        if (category == null ||
                category.trim().isEmpty()) {

            return "OTHER";
        }

        return category
                .trim()
                .toUpperCase();
    }

    // =========================================================
    // ROLE
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

        User user =
                ticket.getUser();

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
                .userId(
                        user != null
                                ? user.getId()
                                : null
                )
                .userName(
                        user != null
                                ? user.getFullName()
                                : null
                )
                .userEmail(
                        user != null
                                ? user.getEmail()
                                : null
                )
                .userRole(
                        user != null
                                ? getUserRole(user)
                                : "USER"
                )
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

        User sender =
                message.getSender();

        return SupportMessageResponse.builder()
                .id(message.getId())
                .ticketId(
                        message.getTicket() != null
                                ? message.getTicket().getId()
                                : null
                )
                .senderId(
                        sender != null
                                ? sender.getId()
                                : null
                )
                .senderName(
                        sender != null
                                ? sender.getFullName()
                                : "Unknown"
                )
                .senderRole(
                        message.getSenderRole()
                )
                .message(
                        message.getMessage()
                )
                .createdAt(
                        message.getCreatedAt()
                )
                .build();
    }
}