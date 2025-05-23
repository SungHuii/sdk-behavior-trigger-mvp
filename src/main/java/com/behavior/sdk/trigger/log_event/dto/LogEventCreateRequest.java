package com.behavior.sdk.trigger.log_event.dto;

import com.behavior.sdk.trigger.log_event.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LogCreateRequest", description = "로그 생성 요청 payload")
public class LogEventCreateRequest {

   @Schema(description = "이벤트 타입", example = "page_view")
   private EventType eventType;

   @Schema(description = "이벤트 발생 시각", example = "2023-10-01T12:00:00")
   private LocalDateTime occurredAt;

   @Schema(description = "이벤트 발생한 URL", example = "https://example.com")
   private String pageUrl;

   @Schema(description = "Condition ID", example = "123e4567-e89b-12d3-a456-426614174000")
   private UUID conditionId;
}
