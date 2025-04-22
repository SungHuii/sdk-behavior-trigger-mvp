package com.behavior.sdk.trigger.visitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "방문자 응답 DTO")
public class VisitorResponse {

   @Schema(description = "방문자 ID", example = "b2c4d5e6-f7g8-9h0i-j1k2-l3m4n5o6p7q8")
   private UUID id;
   @Schema(description = "방문자 키", example = "visitor-key-12345")
   private String visitorKey;
   @Schema(description = "프로젝트 ID", example = "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6")
   private UUID projectId;
   @Schema(description = "방문자 생성일", example = "2023-10-01T12:00:00")
   private LocalDateTime createdAt;

}
