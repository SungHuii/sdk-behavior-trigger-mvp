package com.behavior.sdk.trigger.email_template.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(name = "EmailTemplateResponse", description = "이메일 템플릿 응답 DTO")
public class EmailTemplateResponse {

    @Schema(description = "템플릿 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID id;

    @Schema(description = "조건 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID conditionId;

    @Schema(description = "이메일 제목", example = "OOO 이벤트 안내 메일")
    private String subject;

    @Schema(description = "이메일 본문", example = "안녕하세요. {{visitorName}}님! OOO 이벤트에 초대합니다.")
    private String body;

    @Schema(description = "생성 시각", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "삭제 시각(soft delete)", example = "2023-10-01T12:00:00", nullable = true)
    private LocalDateTime deletedAt;
}
