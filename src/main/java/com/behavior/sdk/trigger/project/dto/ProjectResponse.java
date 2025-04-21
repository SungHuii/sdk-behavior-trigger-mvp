package com.behavior.sdk.trigger.project.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private UUID id;
    private String name;
    private String sdkKey;
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
