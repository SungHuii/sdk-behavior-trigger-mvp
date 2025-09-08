package com.behavior.sdk.trigger.common.exception;

public record FieldErrorDetail(
        String field,   // ex: "minEmails"
        String issue,   // ex: "required >= 50"
        Object rejected // ex: 30
) { }
