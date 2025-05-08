package com.behavior.sdk.trigger.email_template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateResponse {

    private UUID id;
    private UUID conditionId;
    private String subject;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
