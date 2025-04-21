package com.behavior.sdk.trigger.log.dto;

import com.behavior.sdk.trigger.log.enums.EventType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogCreateRequest {


   @NotNull
   private EventType eventType;
   private Long durationMs;
   private LocalDateTime occurredAt;
}
