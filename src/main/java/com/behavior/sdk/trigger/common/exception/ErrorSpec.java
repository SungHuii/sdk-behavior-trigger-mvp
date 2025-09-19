package com.behavior.sdk.trigger.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorSpec {

    // ===== AUTH (1000대) =====
    AUTH_UNAUTHORIZED_ORIGIN("AUTH", "UNAUTHORIZED_ORIGIN",
            "AUTH-UNAUTHORIZED_ORIGIN", 1001, HttpStatus.FORBIDDEN,
            "허용되지 않은 도메인입니다.", "접근 권한이 없는 도메인 요청"),
    AUTH_MISSING_API_KEY("AUTH", "MISSING_API_KEY",
            "AUTH-MISSING_API_KEY", 1002, HttpStatus.UNAUTHORIZED,
            "프로젝트 키가 필요합니다.", "인증 정보 누락"),
    AUTH_INVALID_API_KEY("AUTH", "INVALID_API_KEY",
            "AUTH-INVALID_API_KEY", 1003, HttpStatus.UNAUTHORIZED,
            "유효하지 않은 프로젝트 키입니다.", "인증 실패"),
    AUTH_INVALID_TOKEN("AUTH", "INVALID_TOKEN",
            "AUTH-INVALID_TOKEN", 1004, HttpStatus.UNAUTHORIZED,
            "유효하지 않은 토큰입니다.", "인증 실패"),
    AUTH_EXPIRED_TOKEN("AUTH", "EXPIRED_TOKEN",
            "AUTH-EXPIRED_TOKEN", 1005, HttpStatus.UNAUTHORIZED,
            "만료된 토큰입니다.", "인증 만료"),
    AUTH_INVALID_CREDENTIALS("AUTH", "INVALID_CREDENTIALS",
            "AUTH-INVALID_CREDENTIALS", 1006, HttpStatus.UNAUTHORIZED,
            "이메일 또는 비밀번호가 올바르지 않습니다.", "인증 실패"),

    // ===== VALID (2000대) =====
    VALID_BODY_VALIDATION_FAILED("VALID", "BODY_VALIDATION_FAILED",
            "VALID-BODY_VALIDATION_FAILED", 2001, HttpStatus.BAD_REQUEST,
            "입력값 검증에 실패했습니다.", "요청 검증 실패"),
    VALID_PARAM_VALIDATION_FAILED("VALID", "PARAM_VALIDATION_FAILED",
            "VALID-PARAM_VALIDATION_FAILED", 2002, HttpStatus.BAD_REQUEST,
            "요청 파라미터가 올바르지 않습니다.", "요청 검증 실패"),
    VALID_UNSUPPORTED_MEDIA_TYPE("VALID", "UNSUPPORTED_MEDIA_TYPE",
            "VALID-UNSUPPORTED_MEDIA_TYPE", 2003, HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "지원하지 않는 콘텐츠 유형입니다.", "지원 불가"),

    // ===== COND (3000대) =====
    COND_CONDITION_NOT_FOUND("COND", "CONDITION_NOT_FOUND",
            "COND-CONDITION_NOT_FOUND", 3001, HttpStatus.NOT_FOUND,
            "조건 정보를 찾을 수 없습니다.", "리소스를 찾을 수 없습니다"),
    COND_CONDITION_DISABLED("COND", "CONDITION_DISABLED",
            "COND-CONDITION_DISABLED", 3002, HttpStatus.UNPROCESSABLE_ENTITY,
            "비활성화된 조건입니다.", "현재 상태에서 처리할 수 없습니다"),
    COND_MIN_EMAILS_INVALID("COND", "MIN_EMAILS_INVALID",
            "COND-MIN_EMAILS_INVALID", 3003, HttpStatus.BAD_REQUEST,
            "minEmails는 0 이상이어야 합니다.", "잘못된 요청"),
    COND_MIN_EMAILS_NOT_REACHED("COND", "MIN_EMAILS_NOT_REACHED",
            "COND-MIN_EMAILS_NOT_REACHED", 3004, HttpStatus.UNPROCESSABLE_ENTITY,
            "세그먼트 인원이 minEmails 기준에 미달합니다.", "현재 상태에서 처리할 수 없습니다"),
    COND_DUPLICATE_CONDITION_NAME("COND", "DUPLICATE_CONDITION_NAME",
            "COND-DUPLICATE_CONDITION_NAME", 3005, HttpStatus.CONFLICT,
            "이미 존재하는 조건 이름입니다.", "중복 오류"),

    // ===== SEG (4000대) =====
    SEG_SEGMENT_NOT_FOUND("SEG", "SEGMENT_NOT_FOUND",
            "SEG-SEGMENT_NOT_FOUND", 4001, HttpStatus.NOT_FOUND,
            "세그먼트를 찾을 수 없습니다.", "리소스를 찾을 수 없습니다"),
    SEG_SEGMENT_LOCKED("SEG", "SEGMENT_LOCKED",
            "SEG-SEGMENT_LOCKED", 4002, HttpStatus.LOCKED,
            "현재 세그먼트를 수정할 수 없습니다.", "현재 상태에서 처리할 수 없습니다"),
    SEG_MEMBER_ADD_FAILED("SEG", "MEMBER_ADD_FAILED",
            "SEG-MEMBER_ADD_FAILED", 4003, HttpStatus.CONFLICT,
            "세그먼트 멤버 추가에 실패했습니다.", "처리 실패"),
    SEG_MEMBER_DUPLICATED("SEG", "MEMBER_DUPLICATED",
            "SEG-MEMBER_DUPLICATED", 4004, HttpStatus.CONFLICT,
            "이미 세그먼트에 포함된 사용자입니다.", "중복 오류"),

    // ===== LOG (5000대) =====
    LOG_INVALID_EVENT_SCHEMA("LOG", "INVALID_EVENT_SCHEMA",
            "LOG-INVALID_EVENT_SCHEMA", 5001, HttpStatus.BAD_REQUEST,
            "이벤트 스키마가 올바르지 않습니다.", "요청 검증 실패"),
    LOG_SDK_VERSION_UNSUPPORTED("LOG", "SDK_VERSION_UNSUPPORTED",
            "LOG-SDK_VERSION_UNSUPPORTED", 5002, HttpStatus.UPGRADE_REQUIRED,
            "지원하지 않는 SDK 버전입니다.", "업데이트 필요"),
    LOG_PROJECT_NOT_ALLOWED("LOG", "PROJECT_NOT_ALLOWED",
            "LOG-PROJECT_NOT_ALLOWED", 5003, HttpStatus.FORBIDDEN,
            "프로젝트에서 허용되지 않은 요청입니다.", "접근 거부"),
    LOG_VISITOR_DUPLICATED("LOG", "VISITOR_DUPLICATED",
            "LOG-VISITOR_DUPLICATED", 5004, HttpStatus.CONFLICT,
            "방문자 식별이 중복되었습니다.", "중복 오류"),
    LOG_VISITOR_NOT_FOUND("LOG", "VISITOR_NOT_FOUND",
            "LOG-VISITOR_NOT_FOUND", 5005, HttpStatus.NOT_FOUND,
            "방문자 정보를 찾을 수 없습니다.", "리소스를 찾을 수 없습니다"),
    LOG_PROJECT_NOT_FOUND("LOG", "PROJECT_NOT_FOUND",
            "LOG-PROJECT_NOT_FOUND", 5006, HttpStatus.NOT_FOUND,
            "프로젝트 정보를 찾을 수 없습니다.", "리소스를 찾을 수 없습니다"),

    // ===== MAIL (6000대) =====
    MAIL_EMAIL_PROVIDER_TIMEOUT("MAIL", "EMAIL_PROVIDER_TIMEOUT",
            "MAIL-EMAIL_PROVIDER_TIMEOUT", 6001, HttpStatus.SERVICE_UNAVAILABLE,
            "이메일 서비스 응답이 지연되고 있습니다.", "서비스 지연"),
    MAIL_EMAIL_PROVIDER_ERROR("MAIL", "EMAIL_PROVIDER_ERROR",
            "MAIL-EMAIL_PROVIDER_ERROR", 6002, HttpStatus.BAD_GATEWAY,
            "이메일 서비스 호출에 실패했습니다.", "서비스 오류"),
    MAIL_EMAIL_BOUNCED("MAIL", "EMAIL_BOUNCED",
            "MAIL-EMAIL_BOUNCED", 6003, HttpStatus.UNPROCESSABLE_ENTITY,
            "수신자 주소로 이메일을 전달할 수 없습니다.", "메일 반송"),
    MAIL_TEMPLATE_NOT_FOUND("MAIL", "TEMPLATE_NOT_FOUND",
            "MAIL-TEMPLATE_NOT_FOUND", 6004, HttpStatus.NOT_FOUND,
            "이메일 템플릿을 찾을 수 없습니다.", "리소스를 찾을 수 없습니다"),
    MAIL_SENDING_RATE_LIMITED("MAIL", "SENDING_RATE_LIMITED",
            "MAIL-SENDING_RATE_LIMITED", 6005, HttpStatus.TOO_MANY_REQUESTS,
            "이메일 발송 한도를 초과했습니다.", "요청 제한"),

    // ===== RATE (7000대) =====
    RATE_TOO_MANY_REQUESTS("RATE", "TOO_MANY_REQUESTS",
            "RATE-TOO_MANY_REQUESTS", 7001, HttpStatus.TOO_MANY_REQUESTS,
            "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요.", "요청 제한"),

    // ===== USER (8000대) =====
    USER_EMAIL_DUPLICATED("USER", "EMAIL_DUPLICATED",
            "USER-EMAIL_DUPLICATED", 8001, HttpStatus.CONFLICT,
            "이미 사용 중인 이메일입니다.", "중복 오류"),
    USER_NOT_FOUND("USER", "USER_NOT_FOUND",
            "USER-USER_NOT_FOUND", 8002, HttpStatus.NOT_FOUND,
            "사용자 정보를 찾을 수 없습니다.", "리소스를 찾을 수 없습니다"),


    // ===== SYS (9000대) =====
    SYS_INTERNAL_ERROR("SYS", "INTERNAL_ERROR",
            "SYS-INTERNAL_ERROR", 9000, HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 내부 오류가 발생했습니다.", "서버 내부 오류"),
    SYS_DOWNSTREAM_UNAVAILABLE("SYS", "DOWNSTREAM_UNAVAILABLE",
            "SYS-DOWNSTREAM_UNAVAILABLE", 9001, HttpStatus.SERVICE_UNAVAILABLE,
            "외부 의존 서비스가 응답하지 않습니다.", "외부 서비스 오류"),
    SYS_METHOD_NOT_ALLOWED("SYS", "METHOD_NOT_ALLOWED",
            "SYS-METHOD_NOT_ALLOWED", 9002, HttpStatus.METHOD_NOT_ALLOWED,
            "지원하지 않는 HTTP 메서드입니다.", "지원 불가"),
    SYS_FILE_UPLOAD_FAILED("SYS", "FILE_UPLOAD_FAILED",
            "SYS-FILE_UPLOAD_FAILED", 9003, HttpStatus.INTERNAL_SERVER_ERROR,
            "파일 업로드에 실패했습니다.", "처리 실패"),
    SYS_FILE_NOT_FOUND("SYS", "FILE_NOT_FOUND",
            "SYS-FILE_NOT_FOUND", 9004, HttpStatus.NOT_FOUND,
            "파일을 찾을 수 없습니다.", "리소스를 찾을 수 없습니다");
    private final String domain;
    private final String key;
    private final String code;
    private final int numeric;
    private final HttpStatus httpStatus;
    private final String koMessage;
    private final String defaultTitle;

    ErrorSpec(String domain, String key, String code, int numeric, HttpStatus httpStatus, String koMessage, String defaultTitle) {
        this.domain = domain;
        this.key = key;
        this.code = code;
        this.numeric = numeric;
        this.httpStatus = httpStatus;
        this.koMessage = koMessage;
        this.defaultTitle = defaultTitle;
    }

    public String domain() { return domain;}
    public String key() { return key; }
    public String code() { return code; }
    public int numeric() { return numeric; }
    public HttpStatus httpStatus() { return httpStatus; }
    public String koMessage() { return koMessage; }
    public String defaultTitle() { return defaultTitle; }

}
