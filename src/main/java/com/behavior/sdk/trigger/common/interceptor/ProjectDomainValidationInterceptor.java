package com.behavior.sdk.trigger.common.interceptor;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectDomainValidationInterceptor implements HandlerInterceptor {

    private final ProjectRepository projectRepository;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        log.info("[Interceptor] 요청 URI: {}", request.getRequestURI());

        String uri = request.getRequestURI();
        if (uri.contains("/logs") || uri.contains("/logs.html")) {
            log.info("[Interceptor] log 페이지 접근 허용 (도메인 검증 생략");
            return true;
        }

        // projectId 검증
        String projectIdStr = request.getParameter("projectId");
        if (projectIdStr == null) {
            log.warn("[Interceptor] projectId 파라미터 누락");
            throw new ServiceException(
                ErrorSpec.VALID_PARAM_VALIDATION_FAILED, // 2002, 400
                "프로젝트 ID는 필수입니다.",
                List.of(new FieldErrorDetail("projectId", "required", null))
            );
        }

        UUID projectId;
        try {
            projectId = UUID.fromString(projectIdStr);
        } catch (IllegalArgumentException e) {
            log.warn("[Interceptor] projectId 형식 오류: {}", projectIdStr);
            throw new ServiceException(
                ErrorSpec.VALID_PARAM_VALIDATION_FAILED, // 2002, 400
                "projectId 형식이 올바르지 않습니다.",
                List.of(new FieldErrorDetail("projectId", "invalid format", projectIdStr))
            );
        }

        // 프로젝트 조회
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            log.warn("[Interceptor] 존재하지 않는 프로젝트: {}", projectId);
            throw new ServiceException(
                ErrorSpec.LOG_PROJECT_NOT_FOUND,
                "프로젝트를 찾을 수 없습니다.",
                List.of(new FieldErrorDetail("projectId", "not found", projectId))
            );
        }

        Project project = projectOpt.get();
        List<String> allowedDomains = project.getAllowedDomains();
        log.info("[Interceptor] 등록된 도메인들: {}", allowedDomains);

        // 헤더에서 도메인 추출
        String xDomain = request.getHeader("X-Domain");
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        String domainToCheck = (xDomain != null && !xDomain.isBlank())
                ? xDomain
                : (origin != null ? origin : (referer != null ? referer : ""));

        if (domainToCheck.isBlank()) {
            log.warn("[Interceptor] 도메인 정보가 없습니다 (X-Domain, Origin, Referer 모두 없음)");
            throw new ServiceException(
                ErrorSpec.AUTH_UNAUTHORIZED_ORIGIN, // 1001, 403
                "도메인 정보가 없습니다.",
                List.of(new FieldErrorDetail("domain", "missing", null))
            );
        }

        String normalizedRequestUrl = normalizeUrlWithoutProtocol(domainToCheck);

        boolean isAllowed = allowedDomains.stream()
                .map(this::normalizeUrlWithoutProtocol)
                .anyMatch(allowed -> allowed.equals(normalizedRequestUrl));

        if (!isAllowed) {
            log.warn("[Interceptor] 요청 URL 불일치 - 요청: {}, 허용 목록: {}", normalizedRequestUrl, allowedDomains);
            throw new ServiceException(
                ErrorSpec.AUTH_UNAUTHORIZED_ORIGIN, // 1001, 403
                "허용되지 않은 도메인입니다.",
                List.of(new FieldErrorDetail("requestDomain", "not allowed", normalizedRequestUrl),
                        new FieldErrorDetail("allowedDomains", "configured", allowedDomains))
            );
        }

        log.info("[Interceptor] 도메인 검증 성공: {}", normalizedRequestUrl);
        return true;
    }

    private String normalizeUrlWithoutProtocol(String raw) {
        if (raw == null || raw.isBlank()) return "";
        return raw.toLowerCase()
                .replaceFirst("^https?://", "")   // http:// 또는 https:// 제거
                .replaceAll("/+$", "");          // 끝나는 / 제거
    }
}
