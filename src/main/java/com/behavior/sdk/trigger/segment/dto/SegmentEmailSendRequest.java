package com.behavior.sdk.trigger.segment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "이메일 전송 요청 payload")
public class SegmentEmailSendRequest {

    @NotNull
    @Schema(description = "템플릿 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID templateId;
}
