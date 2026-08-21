package com.bprflavorshub.bpr_flavors_hub.dto.support;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSupportTicketRequest {

    private String status;

    private String priority;

    private String resolution;
}