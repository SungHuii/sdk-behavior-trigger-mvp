package com.behavior.sdk.trigger.project.service;

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
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 프로젝트입니다."));
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
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 프로젝트 입니다."));

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
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 프로젝트입니다."));

        project.softDelete();
        projectRepository.save(project);
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
