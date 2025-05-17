package com.behavior.sdk.trigger.email.controller;

import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.dto.EmailSendResponse;
import com.behavior.sdk.trigger.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 전송 API")
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    @Operation(summary = "이메일 전송", description = "이메일을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 전송 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public ResponseEntity<EmailSendResponse> sendEmail(@Valid @RequestBody EmailSendRequest emailSendRequest) {
        return ResponseEntity.ok(emailService.sendEmail(emailSendRequest));
    }

}
