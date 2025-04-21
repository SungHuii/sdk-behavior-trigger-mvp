package com.behavior.sdk.trigger.log.dto;

import com.behavior.sdk.trigger.log.enums.EventType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogResponse {

   private UUID id;
   private UUID projectId;
   private UUID visitorId;
   private EventType eventType;
   private Long durationMs;
   private LocalDateTime occurredAt;
   private LocalDateTime createdAt;
}
