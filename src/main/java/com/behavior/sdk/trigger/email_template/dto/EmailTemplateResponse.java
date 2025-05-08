package com.behavior.sdk.trigger.email_template.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmailTemplateResponse {

    private UUID id;
    private UUID conditionId;
    private String subject;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
