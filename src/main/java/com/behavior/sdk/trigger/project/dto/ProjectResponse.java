package com.behavior.sdk.trigger.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 응답 payload")
public class ProjectResponse {

    @Schema(description = "프로젝트 ID", example = "3fb8c9e0-4d2f-4a3b-8c9e-0f4a3b8c9e0f")
    private UUID id;
    @Schema(description = "프로젝트 이름", example = "My Project")
    private String name;
    @Schema(description = "프로젝트 SDK Key", example = "3fb8c9e0-4d2f-4a3b-8c9e-0f4a3b8c9e0f")
    private String sdkKey;
    @Schema(description = "프로젝트 소유자 ID", example = "3fb8c9e0-4d2f-4a3b-8c9e-0f4a3b8c9e0f")
    private UUID ownerId;
    @Schema(description = "프로젝트 생성일", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "프로젝트 수정일", example = "2023-10-01T12:00:00")
    private LocalDateTime updatedAt;
}
