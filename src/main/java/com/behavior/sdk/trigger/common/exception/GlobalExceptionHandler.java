package com.behavior.sdk.trigger.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 도메인 / 서비스 계층 예외 (도메인별 커스텀 예외가 ErrorSpec을 품도록함)
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleService(ServiceException ex, HttpServletRequest request) {
        ErrorSpec spec = ex.getSpec();
        ErrorResponse body = ErrorResponses.fromSpec(spec, request, ex.getDetails(), ex.getHint());
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    // 2. @Valid 바인딩 실패 예외 (RequestBody DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        BindingResult br = ex.getBindingResult();
        List<FieldErrorDetail> details = br.getFieldErrors().stream().map(this::toDetail).toList();
        ErrorSpec spec = ErrorSpec.VALID_BODY_VALIDATION_FAILED;
        ErrorResponse body = ErrorResponses.fromSpec(spec, request, details, "요청 본문 값을 확인해주세요.");
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    // 3. @Valid 바인딩 실패 (Query, Form 파라미터)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBind(BindException ex, HttpServletRequest request) {
        List<FieldErrorDetail> details = ex.getFieldErrors().stream().map(this::toDetail).toList();
        ErrorSpec spec = ErrorSpec.VALID_PARAM_VALIDATION_FAILED;
        ErrorResponse body = ErrorResponses.fromSpec(spec, request, details, "요청 파라미터를 확인해주세요.");
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    // 4. 제약조건 위반 (메서드 파라미터 검증)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex, HttpServletRequest request) {
        List<FieldErrorDetail> details = ex.getConstraintViolations().stream().map(this::toDetail).toList();
        ErrorSpec spec = ErrorSpec.VALID_PARAM_VALIDATION_FAILED;
        ErrorResponse body = ErrorResponses.fromSpec(spec, request, details, null);
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    // 5. 타입 변환 실패 (예: UUID/숫자 파싱 실패)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest req) {
        ErrorSpec spec = ErrorSpec.VALID_PARAM_VALIDATION_FAILED;
        FieldErrorDetail d = new FieldErrorDetail(ex.getName(), "type mismatch", ex.getValue());
        ErrorResponse body = ErrorResponses.fromSpec(spec, req, List.of(d), null);
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    // 6. JSON 파싱/본문 없음
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        ErrorSpec spec = ErrorSpec.VALID_BODY_VALIDATION_FAILED;
        ErrorResponse body = ErrorResponses.fromSpec(spec, req);
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    // 7. 필요한 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest req) {
        ErrorSpec spec = ErrorSpec.VALID_PARAM_VALIDATION_FAILED;
        FieldErrorDetail d = new FieldErrorDetail(ex.getParameterName(), "missing parameter", null);
        ErrorResponse body = ErrorResponses.fromSpec(spec, req, List.of(d), null);
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    // 8. 미디어 타입 불일치
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMedia(HttpMediaTypeNotSupportedException ex,
                                                                HttpServletRequest req) {
        ErrorSpec spec = ErrorSpec.VALID_UNSUPPORTED_MEDIA_TYPE;
        ErrorResponse body = ErrorResponses.fromSpec(spec, req);
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    // 9. 인증/인가
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        // 기본값: 토큰 문제로 간주
        ErrorSpec spec = ErrorSpec.AUTH_INVALID_TOKEN;
        ErrorResponse body = ErrorResponses.fromSpec(spec, req, null, "인증 정보를 다시 확인해 주세요.");
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        // 접근 거부는 Forbidden
        ErrorSpec spec = ErrorSpec.AUTH_UNAUTHORIZED_ORIGIN; // 정책에 맞게 다른 AUTH_*로 바꿔도 됨
        ErrorResponse body = ErrorResponses.fromSpec(spec, req, null, "접근 권한이 없습니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // --- 10) 리소스 없음 (Spring 6) ---
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex, HttpServletRequest req) {
        ErrorSpec spec = ErrorSpec.SYS_FILE_NOT_FOUND; // 404 표준 스펙 재사용 (일반 엔드포인트 404면 다른 404 스펙 만들어도 OK)
        ErrorResponse body = ErrorResponses.fromSpec(spec, req);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // --- 11) 마지막 방어선 ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleEtc(Exception ex, HttpServletRequest req) {
        ErrorSpec spec = ErrorSpec.SYS_INTERNAL_ERROR;
        ErrorResponse body = ErrorResponses.fromSpec(spec, req);
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleRSE(ResponseStatusException ex,
                                                   HttpServletRequest req) {
        ErrorSpec spec = switch (ex.getStatusCode().value()) {
            case 405 -> ErrorSpec.SYS_METHOD_NOT_ALLOWED;
            case 404 -> ErrorSpec.SYS_FILE_NOT_FOUND;
            default -> ErrorSpec.SYS_INTERNAL_ERROR;
        };
        return ResponseEntity.status(ex.getStatusCode())
                .body(ErrorResponses.fromSpec(spec, req, null, ex.getReason()));
    }

    // 헬퍼 메서드
    private FieldErrorDetail toDetail(FieldError fe) {
        Object rejected = fe.getRejectedValue();
        String issue = fe.getDefaultMessage(); // Bean Validation 어노테이션 메시지
        return new FieldErrorDetail(fe.getField(), issue, rejected);
    }

    private FieldErrorDetail toDetail(ConstraintViolation<?> cv) {
        String field = cv.getPropertyPath() == null ? "" : cv.getPropertyPath().toString();
        Object rejected = cv.getInvalidValue();
        String issue = cv.getMessage();
        return new FieldErrorDetail(field, issue, rejected);
    }
}
