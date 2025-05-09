package com.behavior.sdk.trigger.condition.dto;

import com.behavior.sdk.trigger.log_event.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ConditionCreateRequest", description = "신규 Condition 생성 요청 payload")
public class ConditionCreateRequest {

    @NotNull
    @Schema(description = "프로젝트 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID projectId;

    @NotNull
    @Schema(description = "이벤트 타입", example = "PAGE_VIEW")
    private EventType eventType;

    @NotBlank
    @Schema(description = "비교 연산자", example = "GREATER_THAN")
    private String operator;

    @NotNull
    @Min(0)
    @Schema(description = "임계값", example = "60")
    private Integer threshold;

    @NotBlank
    @Schema(description = "페이지 URL", example = "https://example.com")
    private String pageUrl;

}
