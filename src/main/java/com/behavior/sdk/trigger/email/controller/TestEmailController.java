package com.behavior.sdk.trigger.email.controller;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.email.dto.SimpleEmailRequest;
import com.behavior.sdk.trigger.email.service.SendGridEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test-emails")
@RequiredArgsConstructor
@Tag(name = "TestEmail", description = "테스트용 단순 이메일 전송 API")
@Validated
public class TestEmailController {

    private final SendGridEmailService sendGridEmailService;

    public record TestEmailRequest(
            @NotBlank @Email String email,
            String subject,
            String body
    ) {}

    @PostMapping
    @Operation(summary = "테스트용 이메일 전송", description = "간단한 테스트용 이메일을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 전송 성공")
    public ResponseEntity<Map<String, Object>> sendTestEmail(@RequestBody TestEmailRequest request) {
        if (request.email() == null || request.email().isBlank()) {
            throw new ServiceException(
                    ErrorSpec.VALID_PARAM_VALIDATION_FAILED,
                    "이메일 주소를 입력해주세요.",
                    List.of(new FieldErrorDetail("email", "blank", null))
            );
        }

        String subject = (request.subject() == null || request.subject().isBlank())
                ? "[Trigger] 테스트 이메일"
                : request.subject();

        String body = (request.body() == null || request.body().isBlank())
                ? "이것은 Trigger 시스템에서 전송된 테스트 이메일입니다."
                : request.body();

        sendGridEmailService.sendEmail(request.email(), subject, body);

        return ResponseEntity.ok(Map.of(
                "status", "SENT", "to", request.email(), "subject", subject
        ));
    }
}
