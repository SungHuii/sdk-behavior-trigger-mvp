package com.behavior.sdk.trigger.common.interceptor;

import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectDomainValidationInterceptor implements HandlerInterceptor {

    /*
    * 이 인터셉터는 프로젝트 ID와 도메인 일치를 검증합니다.
    * 요청 파라미터 : projectId
    * 요청 헤더 : Origin 또는 Referer
    * */

    private final ProjectRepository projectRepository;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String projectIdStr = request.getParameter("projectId");
        if (projectIdStr == null ) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing projectId parameter");
            return false;
        }

        UUID projectId;
        try {
            projectId = UUID.fromString(projectIdStr);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid projectId format");
            return false;
        }

        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Project not found");
            return false;
        }

        Project project = projectOpt.get();

        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        String domainToCheck = (origin != null) ? origin : (referer != null ? referer : "");

        if (!domainToCheck.contains(project.getDomain())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Domain doesn't match project domain");
            return false;
        }

        return true;
    }
}
