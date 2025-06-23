package com.behavior.sdk.trigger.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "SimpleEmailRequest", description = "간단한 테스트용 이메일 요청 payload")
public class SimpleEmailRequest {

    @NotBlank
    @Email
    @Schema(description = "받는 사람의 이메일 주소", example = "user@example.com")
    private String email;
}
