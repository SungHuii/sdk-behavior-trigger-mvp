package com.behavior.sdk.trigger.email_template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateCreateRequest {

    @NotNull
    private UUID conditionId;
    @NotBlank
    private String subject;
    @NotBlank
    private String body;
}
