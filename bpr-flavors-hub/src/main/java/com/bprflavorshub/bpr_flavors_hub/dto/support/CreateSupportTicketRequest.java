package com.bprflavorshub.bpr_flavors_hub.dto.support;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSupportTicketRequest {

    private String subject;

    private String description;

    private String category;

    private String priority;
}