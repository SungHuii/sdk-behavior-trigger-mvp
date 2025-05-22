package com.behavior.sdk.trigger.log_event.dto;

import com.behavior.sdk.trigger.log_event.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LogResponse", description = "로그 응답 객체")
public class LogEventResponse {

   @Schema(description = "로그 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
   private UUID id;

   @Schema(description = "프로젝트 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
   private UUID projectId;

   @Schema(description = "방문자 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
   private UUID visitorId;

   @Schema(description = "Condition ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
   private UUID conditionId;

   @Schema(description = "이벤트 타입", example = "page_view")
   private EventType eventType;

   @Schema(description = "이벤트 발생 시각", example = "2023-10-01T12:00:00")
   private LocalDateTime occurredAt;

   @Schema(description = "로그 생성 시각", example = "2023-10-01T12:00:00")
   private LocalDateTime createdAt;

   @Schema(description = "이벤트 발생 URL", example = "https://example.com")
   private String pageUrl;
}
