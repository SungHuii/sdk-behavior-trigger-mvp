package com.behavior.sdk.trigger.condition.dto;

import com.behavior.sdk.trigger.log_event.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ConditionResponse", description = "Condition 응답 객체")
public class ConditionResponse {

    @Schema(description = "Condition ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID id;

    @Schema(description = "프로젝트 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID projectId;

    @Schema(description = "이벤트 타입", example = "PAGE_VIEW")
    private EventType eventType;

    @Schema(description = "비교 연산자", example = "GREATER_THAN")
    private String operator;

    @Schema(description = "임계값", example = "60")
    private Integer threshold;

    @Schema(description = "페이지 URL", example = "https://example.com")
    private String pageUrl;

    @Schema(description = "Condition 생성 시각", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Condition 삭제 시각", example = "2023-10-01T12:00:00")
    private LocalDateTime deletedAt;
}
