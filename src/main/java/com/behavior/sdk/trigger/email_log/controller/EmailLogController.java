package com.behavior.sdk.trigger.email_log.controller;

import com.behavior.sdk.trigger.email_log.dto.EmailLogResponse;
import com.behavior.sdk.trigger.email_log.service.EmailLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/email-logs")
@RequiredArgsConstructor
public class EmailLogController {

    private final EmailLogService emailLogService;

    @GetMapping
    @Operation(summary = "이메일 로그 조회", description = "방문자 ID로 이메일 로그를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 로그 조회 성공")
    public List<EmailLogResponse> listByVisitorId(@RequestParam("visitorId")UUID visitorId) {
        return emailLogService
                .listLogsByVisitorId(visitorId)
                .stream()
                .map(EmailLogResponse::logResponse)
                .toList();
    }

    @DeleteMapping("/{logId}")
    @Operation(summary = "이메일 로그 삭제(soft delete)", description = "이메일 로그를 soft delete 합니다.")
    @ApiResponse(responseCode = "204", description = "이메일 로그 삭제 성공")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmailLog(@PathVariable UUID logId) {
        emailLogService.softDeleteEmailLog(logId);
    }
}
