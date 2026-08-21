package com.bprflavorshub.bpr_flavors_hub.dto.support;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicketResponse {

    private Long id;

    private Long userId;

    private String userName;

    private String userEmail;

    private String userRole;

    private String subject;

    private String description;

    private String category;

    private String priority;

    private String status;

    private String resolution;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    private List<SupportMessageResponse> messages;
}