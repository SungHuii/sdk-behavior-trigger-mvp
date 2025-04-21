package com.behavior.sdk.trigger.visitor.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorResponse {

   private UUID id;
   private String visitorKey;
   private UUID projectId;
   private LocalDateTime createdAt;

}
