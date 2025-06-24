package com.behavior.sdk.trigger.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoginResponse", description = "로그인 응답 payload")
public class LoginResponse {

    @Schema(description = "사용자 토큰", example = "eyJhbGciO...")
    private String token;
}
