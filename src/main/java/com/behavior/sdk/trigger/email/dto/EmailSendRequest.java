package com.behavior.sdk.trigger.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EmailSendRequest", description = "이메일 전송 요청 payload")
public class EmailSendRequest {

    @NotNull
    @Schema(name = "visitorId", description = "방문자 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID visitorId;

//    @NotNull
    @Schema(name = "templateId", description = "템플릿 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID templateId;

}
