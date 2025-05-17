package com.behavior.sdk.trigger.email.dto;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EmailSendResponse", description = "이메일 전송 응답 객체")
public class EmailSendResponse {

    @NotNull
    @Schema(name = "logId", description = "로그 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID logId;

    @NotNull
    @Schema(name = "status", description = "이메일 전송 상태", example = "SUCCESS")
    private EmailStatus status;

    @Schema(name = "sentAt", description = "이메일 전송 시각", example = "2023-10-01T12:00:00")
    private LocalDateTime sentAt;
}
