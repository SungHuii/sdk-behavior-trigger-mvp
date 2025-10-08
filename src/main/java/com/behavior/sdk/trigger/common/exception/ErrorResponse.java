package com.behavior.sdk.trigger.common.exception;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "에러 발생 시각", example = "2025-09-19T12:00:00Z")
        String timestamp,
        @Schema(description = "트레이스 상관관계 ID", example = "req-abc123")
        String traceId,                     // MDC에서 꺼내도 되고, null 허용
        // MCD(Mapped Diagnostic Context)
        // SLF4J와 같은 로깅 프레임워크에서 제공하는 기능으로,
        // 현재 실행 중인 쓰레드에 특정 데이터(예: 사용자 ID, 요청 ID)를 저장하고 관리
        @Schema(description = "요청 경로", example = "/api/segments/trigger-email")
        String path,
        @Schema(description = "도메인+키 형태의 에러코드", example = "COND-MIN_EMAILS_NOT_REACHED")
        String code,
        @Schema(description = "에러 키(도메인 제외)", example = "MIN_EMAILS_NOT_REACHED")
        String errorKey,
        @Schema(description = "숫자 에러 코드", example = "3004")
        int numeric,
        @Schema(description = "타이틀(요약 메시지)", example = "현재 상태에서 처리할 수 없습니다.")
        String title,
        @Schema(description = "자세한 메시지", example = "세그먼트 인원이 minEmails 기준에 미달합니다.")
        String message,
        @Schema(description = "필드/도메인 상세")
        List<FieldErrorDetail> details,
        @Schema(description = "사용자 힌트", example = "대상 인원이 기준을 충족한 후 다시 시도해 주세요.")
        String hint
) {}
