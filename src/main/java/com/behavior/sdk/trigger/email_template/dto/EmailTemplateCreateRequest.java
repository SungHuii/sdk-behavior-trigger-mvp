package com.behavior.sdk.trigger.email_template.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(name = "EmailTemplateCreateRequest", description = "새 이메일 템플릿 생성 요청 DTO")
public class EmailTemplateCreateRequest {

    @NotNull
    @Schema(description = "조건 ID (Condition Id)",example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID conditionId;

    @NotBlank
    @Schema(description = "이메일 제목", example = "OOO 이벤트 안내 메일")
    private String subject;

    @NotBlank
    @Schema(description = "이메일 본문", example = "안녕하세요. {{visitorName}}님! OOO 이벤트에 초대합니다.")
    private String body;
}
