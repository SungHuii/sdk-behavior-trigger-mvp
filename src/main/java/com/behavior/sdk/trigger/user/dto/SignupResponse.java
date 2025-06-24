package com.behavior.sdk.trigger.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SignupResponse", description = "회원가입 응답 payload")
public class SignupResponse {

    @Schema(description = "사용자 ID", example = "3f8b1c2e-4d3a-4b5e-8c7f-9a0d1e2f3a4b")
    private UUID userId;

    @Schema(description = "사용자 이메일", example = "abcd@example.com")
    private String email;

    @Schema(description = "생성일자", example = "2023-10-01T12:00:00Z")
    private Instant createdAt;

}
