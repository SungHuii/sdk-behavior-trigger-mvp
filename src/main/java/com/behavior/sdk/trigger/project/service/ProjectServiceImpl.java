package com.behavior.sdk.trigger.project.service;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.condition.entity.Condition;
import com.behavior.sdk.trigger.condition.repository.ConditionRepository;
import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
import com.behavior.sdk.trigger.project.dto.ProjectUpdateRequest;
import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import com.behavior.sdk.trigger.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ConditionRepository conditionRepository;

    private ProjectResponse toDto(Project p) {
        return ProjectResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .allowedDomains(p.getAllowedDomains())
                .createdAt(p.getCreatedAt())
                .deletedAt(p.getDeletedAt())
                .build();
    }

    @Override
    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request, User user) {
        Project project = Project.builder()
                .name(request.getName())
                .allowedDomains(request.getAllowedDomains())
                .user(user)
                .build();

        Project savedProject = projectRepository.save(project);
        return toDto(savedProject);
    }

    @Override
    public ProjectResponse getProject(UUID projectId) {
        Project project = projectRepository
                .findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ServiceException(
                        ErrorSpec.LOG_PROJECT_NOT_FOUND,
                        "존재하지 않는 프로젝트 입니다.",
                        List.of(new FieldErrorDetail("projectId", "not found", projectId))
                ));
        return toDto(project);
    }

    @Override
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(UUID projectId, ProjectUpdateRequest request) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ServiceException(
                        ErrorSpec.LOG_PROJECT_NOT_FOUND,
                        "존재하지 않는 프로젝트 입니다.",
                        List.of(new FieldErrorDetail("projectId", "not found", projectId))
                ));

        if (request.getName() != null && !request.getName().isBlank()) {
            project.setName(request.getName());
        }

        if (request.getAllowedDomains() != null && !request.getAllowedDomains().isEmpty()) {
            project.setAllowedDomains(request.getAllowedDomains());
        }

        return toDto(project);
    }

    @Override
    @Transactional
    public void deleteProject(UUID projectId) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new ServiceException(
                        ErrorSpec.LOG_PROJECT_NOT_FOUND,
                        "존재하지 않는 프로젝트 입니다.",
                        List.of(new FieldErrorDetail("projectId", "not found", projectId))
                ));

        project.softDelete();
        projectRepository.save(project);

        /*
         * 프로젝트 삭제 시 해당 프로젝트에 속한 모든 조건을 soft-delete 처리.
         * 추 후, Project, Condition 엔티티에 CascadeType 설정을 통해
         * 조건 삭제를 자동으로 처리할 수 있도록 처리 예정.
         * */
        List<Condition> conditions = conditionRepository.findByProjectId(projectId);
        for (Condition c : conditions) {
            c.softDelete();
            conditionRepository.save(c);
        }
    }

    @Override
    @Transactional
    public List<ProjectResponse> getProjectsByUserId(UUID userId) {
        return projectRepository.findAllByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }
}
