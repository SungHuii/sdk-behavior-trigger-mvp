package com.behavior.sdk.trigger.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 업데이트 요청 payload")
public class ProjectUpdateRequest {

    @Schema(description = "프로젝트 이름", example = "Updated Project Name")
    private String name;
    @Schema(description = "프로젝트 도메인", example = "https://updated-example.com")
    private String domain;
}
