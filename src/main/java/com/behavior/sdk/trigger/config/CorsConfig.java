package com.behavior.sdk.trigger.config;

import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@Profile("!test")
public class CorsConfig {

    private final ProjectRepository projectRepository;

    public CorsConfig(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();

            String origin = request.getHeader("Origin");
            String projectId = request.getParameter("projectId");

            // projectId가 null인 경우, 요청 URI에서 UUID 형식의 projectId 추출
            if (projectId == null) {
                String uri = request.getRequestURI();
                // UUID 정규식으로 projectId 추출
                Pattern uuidPattern = Pattern.compile(
                        "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
                );
                Matcher matcher = uuidPattern.matcher(uri);
                if (matcher.find()) {
                    projectId = matcher.group();
                }
            }

            if (origin != null && projectId != null) {
                try {
                    Optional<Project> projectOpt = projectRepository.findById(UUID.fromString(projectId));
                    if (projectOpt.isPresent() && projectOpt.get().getAllowedDomains().contains(origin)) {
                        config.addAllowedOrigin(origin);
                        config.addAllowedHeader("*");
                        config.addAllowedMethod("*");
                        config.setAllowCredentials(true);
                    }
                } catch (IllegalArgumentException e) {
                    // 잘못된 UUID 형식 등 예외 무시
                }
            }
            return config;
        };
    }
}
