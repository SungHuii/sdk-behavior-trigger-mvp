package com.behavior.sdk.trigger.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateRequest {

    @NotBlank(message = "프로젝트 이름 입력")
    private String name;


}
