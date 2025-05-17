package com.behavior.sdk.trigger.email_template.controller;

import com.behavior.sdk.trigger.email_template.dto.EmailTemplateCreateRequest;
import com.behavior.sdk.trigger.email_template.dto.EmailTemplateResponse;
import com.behavior.sdk.trigger.email_template.entity.EmailTemplate;
import com.behavior.sdk.trigger.email_template.service.EmailTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/email-templates")
@RequiredArgsConstructor
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    @PostMapping
    @Operation(summary = "이메일 템플릿 생성", description = "ConditionId로 새로운 이메일 템플릿을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "이메일 템플릿 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<EmailTemplateResponse> create(
            @RequestBody @Valid EmailTemplateCreateRequest emailTemplateCreateRequest) {
        EmailTemplate emailTemplate = EmailTemplate.builder()
                .conditionId(emailTemplateCreateRequest.getConditionId())
                .subject(emailTemplateCreateRequest.getSubject())
                .body(emailTemplateCreateRequest.getBody())
                .build();

        EmailTemplate savedEmailTemplate = emailTemplateService.createTemplate(emailTemplate);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(savedEmailTemplate));
    }

    @GetMapping("/{conditionId}")
    @Operation(summary = "이메일 템플릿 목록 조회", description = "ConditionId로 해당 이메일 템플릿을 목록 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 템플릿 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "이메일 템플릿을 찾을 수 없음")
    })
    public ResponseEntity<List<EmailTemplateResponse>> listByCondition(@PathVariable UUID conditionId) {
        List<EmailTemplateResponse> emailTemplateList = emailTemplateService.listByCondition(conditionId)
                .stream()
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(emailTemplateList);
    }

    @DeleteMapping("/{templateId}")
    @Operation(summary = "이메일 템플릿 삭제(soft)", description = "템플릿 ID로 특정 이메일 템플릿을 soft delete 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "이메일 템플릿 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "이메일 템플릿을 찾을 수 없음")
    })
    public ResponseEntity<Void> softDeleteTemplate(@PathVariable UUID templateId) {
        emailTemplateService.softDeleteTemplate(templateId);
        return ResponseEntity.noContent().build();
    }

    private EmailTemplateResponse toDto(EmailTemplate emailTemplate) {
        return EmailTemplateResponse.builder()
                .id(emailTemplate.getId())
                .conditionId(emailTemplate.getConditionId())
                .subject(emailTemplate.getSubject())
                .body(emailTemplate.getBody())
                .createdAt(emailTemplate.getCreatedAt())
                .deletedAt(emailTemplate.getDeletedAt())
                .build();
    }
}
