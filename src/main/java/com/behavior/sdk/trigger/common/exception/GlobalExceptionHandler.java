package com.behavior.sdk.trigger.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        BindingResult br = ex.getBindingResult();
        List<FieldErrorDetail> details = br.getFieldErrors().stream().map(this::toDetail).toList();
        ErrorSpec spec = ErrorSpec.VALID_BODY_VALIDATION_FAILED;
        ErrorResponse body = ErrorResponses.fromSpec(spec, request, details, "요청 본문 값을 확인해주세요.");
        return ResponseEntity.status(spec.httpStatus()).body(body);
    }


    private FieldErrorDetail toDetail(ConstraintViolation<?> cv) {
        String field = cv.getPropertyPath() == null ? "" : cv.getPropertyPath().toString();
        Object rejected = cv.getInvalidValue();
        String issue = cv.getMessage();
        return new FieldErrorDetail(field, issue, rejected);
    }
}
