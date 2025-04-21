package com.behavior.sdk.trigger.log.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogCreateRequest {

   private String projectKey;
   private String visitorKey;
   private String eventType;
   private Long durationMs;
   private LocalDateTime occurredAt;
}
