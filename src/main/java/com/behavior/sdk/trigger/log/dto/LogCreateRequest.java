package com.behavior.sdk.trigger.log.dto;

import com.behavior.sdk.trigger.log.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LogCreateRequest", description = "로그 생성 요청 payload")
public class LogCreateRequest {

   @NotNull
   @Schema(description = "이벤트 타입", example = "page_view")
   private EventType eventType;

   @Schema(description = "이벤트 지속 시간(Ms)", example = "1000")
   private Long durationMs;

   @Schema(description = "이벤트 발생 시각", example = "2023-10-01T12:00:00")
   private LocalDateTime occurredAt;
}
