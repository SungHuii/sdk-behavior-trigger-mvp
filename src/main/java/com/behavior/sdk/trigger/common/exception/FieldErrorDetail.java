package com.behavior.sdk.trigger.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;

public record FieldErrorDetail(
        @Schema(description = "필드명", example = "minEmails")
        String field,
        @Schema(description = "문제 요약", example = "required >= 50")
        String issue,
        @Schema(description = "거부된 값", example = "27")
        Object rejected // ex: 30
) { }
