package com.behavior.sdk.trigger.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ErrorResponses {
    private ErrorResponses() {}

    public static ErrorResponse fromSpec(ErrorSpec spec,
                                         HttpServletRequest request,
                                         List<FieldErrorDetail> details,
                                         String hint) {
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        String traceId = MDC.get("traceId"); // null 허용
        String path = request != null ? request.getRequestURI() : null;
        return new ErrorResponse(
                timestamp,
                traceId,
                path,
                spec.code(),
                spec.key(),
                spec.numeric(),
                spec.defaultTitle(),
                spec.koMessage(),
                details,
                hint
        );
    }

    public static ErrorResponse fromSpec(ErrorSpec spec, HttpServletRequest request) {
        return fromSpec(spec, request, null, null);
    }
}
