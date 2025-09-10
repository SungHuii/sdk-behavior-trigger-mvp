package com.behavior.sdk.trigger.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 요청 단위 추적 ID(traceId)를 부여/전파하는 서블릿 필터.
 *
 * <p><b>목적</b>
 * <ul>
 *   <li>모든 요청에 대해 상관관계 식별자(correlation id)를 보장하여
 *       로그, 에러 응답, 클라이언트 재시도 기록을 서로 연결한다.</li>
 * </ul>
 *
 * <p><b>동작</b>
 * <ul>
 *   <li>요청 헤더 <code>X-Request-Id</code>를 읽고, 없으면 UUID를 생성한다.</li>
 *   <li>해당 값을 SLF4J MDC의 <code>"traceId"</code> 키로 저장하여
 *       현재 스레드의 로그에 자동 포함되게 한다.</li>
 *   <li>동일 값을 응답 헤더 <code>X-Request-Id</code>로 돌려줘
 *       클라이언트/게이트웨이가 추적에 활용할 수 있게 한다.</li>
 *   <li>요청 처리 후 <code>finally</code> 블록에서 MDC를 반드시 정리한다.</li>
 * </ul>
 *
 * <p><b>효과</b>
 * <ul>
 *   <li>운영 장애 분석 시 특정 요청의 전체 로그 흐름을 빠르게 모을 수 있다.</li>
 *   <li>다중 서비스/게이트웨이 환경에서 상관관계가 유지된다
 *       (외부에서 전달된 X-Request-Id를 그대로 이어받음).</li>
 *   <li>{@code GlobalExceptionHandler} → {@code ErrorResponses}가
 *       MDC의 traceId를 읽어 에러 응답 본문에도 동일 값을 노출한다.</li>
 * </ul>
 *
 * <p><b>비고</b>
 * <ul>
 *   <li>{@link org.springframework.web.filter.OncePerRequestFilter} 기반으로
 *       요청당 한 번만 실행된다.</li>
 *   <li>{@link #shouldNotFilter(javax.servlet.http.HttpServletRequest)}를 오버라이드해
 *       Swagger/헬스체크 등 제외 경로를 손쉽게 설정할 수 있다.</li>
 * </ul>
 *
 * @see org.slf4j.MDC
 * @see com.behavior.sdk.trigger.common.exception.ErrorResponses
 * @see com.behavior.sdk.trigger.common.exception.GlobalExceptionHandler
 */

public class TraceIdFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-Request-Id";
    public static final String MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        String incoming = request.getHeader(HEADER_NAME);
        String traceId = (incoming != null && !incoming.isBlank()) ? incoming : UUID.randomUUID().toString();

        MDC.put(MDC_KEY, traceId);
        // 응답 헤더에도 실어주면 클라이언트가 추적하기 쉬움
        response.setHeader(HEADER_NAME, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/actuator/health");
    }
}
