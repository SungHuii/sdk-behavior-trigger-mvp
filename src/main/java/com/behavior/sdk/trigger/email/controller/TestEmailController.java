package com.behavior.sdk.trigger.email.controller;

import com.behavior.sdk.trigger.email.dto.SimpleEmailRequest;
import com.behavior.sdk.trigger.email.service.SendGridEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-emails")
@RequiredArgsConstructor
@Tag(name = "TestEmail", description = "테스트용 단순 이메일 전송 API")
public class TestEmailController {

    private final SendGridEmailService sendGridEmailService;

    @PostMapping
    @Operation(summary = "테스트용 이메일 전송", description = "간단한 테스트용 이메일을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 전송 성공")
    public ResponseEntity<Void> sendTestEmail(@Valid @RequestBody SimpleEmailRequest simpleEmailRequest) {

        sendGridEmailService.sendEmail(
                simpleEmailRequest.getEmail(),
                "VisiLog Test Email",
                "VisiLog 테스트 이메일 본문. SendGrid 사용"
        );
        return ResponseEntity.ok().build();
    }
}
