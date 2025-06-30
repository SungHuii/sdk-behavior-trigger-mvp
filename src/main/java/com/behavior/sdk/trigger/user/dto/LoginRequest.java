package com.behavior.sdk.trigger.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoginRequest", description = "로그인 요청 payload")
public class LoginRequest {

    @Email
    @NotBlank
    @Schema(description = "사용자 이메일", example = "john@example.com")
    private String email;

    @NotBlank
    @Schema(description = "사용자 비밀번호", example = "securePassword123")
    private String password;
}
