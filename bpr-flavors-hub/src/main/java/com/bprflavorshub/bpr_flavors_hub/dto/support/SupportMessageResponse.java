package com.bprflavorshub.bpr_flavors_hub.dto.support;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportMessageResponse {

    private Long id;

    private Long ticketId;

    private Long senderId;

    private String senderName;

    private String senderRole;

    private String message;

    private LocalDateTime createdAt;
}