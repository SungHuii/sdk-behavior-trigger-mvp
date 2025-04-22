package com.behavior.sdk.trigger.project.service;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
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

    @Override
    @Transactional
    public ProjectResponse createProject(UUID ownerId, ProjectCreateRequest createRequest) {
        Project project = Project.builder()
                .name(createRequest.getName())
                .ownerId(ownerId)
                .build();

        Project savedProject = projectRepository.save(project);
        return toDto(savedProject);
    }

    @Override
    public ProjectResponse getProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트"));

        return toDto(project);
    }

    @Override
    public List<ProjectResponse> getProjectsByOwner(UUID ownerId) {
        return projectRepository.findAllByOwnerIdAndDeletedAtIsNull(ownerId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void deleteProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 프로젝트"));
        project.softDelete();
        projectRepository.save(project);
    }

    private ProjectResponse toDto(Project p) {
        return ProjectResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .sdkKey(p.getSdkKey())
                .ownerId(p.getOwnerId())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
