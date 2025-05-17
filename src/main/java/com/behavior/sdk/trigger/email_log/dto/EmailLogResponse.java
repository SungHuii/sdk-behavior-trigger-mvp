package com.behavior.sdk.trigger.email_log.dto;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@Schema(name = "EmailLogResponse", description = "이메일 발송 로그 응답 객체")
public class EmailLogResponse {

    @Schema(name = "logId", description = "로그 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID logId;

    @Schema(name = "visitorId", description = "방문자 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID visitorId;

    @Schema(name = "templateId", description = "템플릿 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID templateId;

    @Schema(name = "status", description = "이메일 발송 상태", example = "SENT")
    private EmailStatus status;

    @Schema(name = "createdAt", description = "로그(발송) 생성 시각", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;

    public static EmailLogResponse logResponse(EmailLog log) {
        return EmailLogResponse.builder()
                .logId(log.getId())
                .visitorId(log.getVisitorId())
                .templateId(log.getTemplateId())
                .status(log.getStatus())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
