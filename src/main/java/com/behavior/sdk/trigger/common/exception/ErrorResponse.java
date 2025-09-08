package com.behavior.sdk.trigger.common.exception;

import java.util.List;

public record ErrorResponse(
        String timestamp,                   // ISO-8601 문자열
        String traceId,                     // MDC에서 꺼내도 되고, null 허용
        // MCD(Mapped Diagnostic Context)
        // SLF4J와 같은 로깅 프레임워크에서 제공하는 기능으로,
        // 현재 실행 중인 쓰레드에 특정 데이터(예: 사용자 ID, 요청 ID)를 저장하고 관리
        String path,                        // 요청 경로 Request URI
        String code,                        // 애플리케이션별 에러 코드 (ex: "COND-MIN_EMAILS_NOT_REACHED")
        String errorKey,                    // HTTP 상태 코드에 대응하는 문자열 (ex: "MIN_EMAILS_NOT_REACHED")
        int numeric,                        // 에러 코드 (ex: 3004)
        String title,                       // 사용자/개발자에게 보여줄 에러 제목
        String message,                     // ko 메시지 (사용자에게 보여줄 메시지)
        List<FieldErrorDetail> details,     // 검증/도메인 보조 정보
        String hint                         // 에러 해결을 위한 힌트 (사용자에게 보여줄 메시지)
) {}
