package com.behavior.sdk.trigger.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 생성 요청 payload")
public class ProjectCreateRequest {

    @NotBlank(message = "프로젝트 이름 입력")
    @Schema(description = "프로젝트 이름", example = "My Project")
    private String name;

    @NotBlank(message = "프로젝트 도메인 입력")
    @Schema(description = "프로젝트 도메인", example = "https://example.com")
    private String domain;


}
