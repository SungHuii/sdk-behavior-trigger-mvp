package com.behavior.sdk.trigger.log.dto;

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
   private String eventType;
   private Long durationMs;
   private LocalDateTime occurredAt;
   private LocalDateTime createdAt;
}
