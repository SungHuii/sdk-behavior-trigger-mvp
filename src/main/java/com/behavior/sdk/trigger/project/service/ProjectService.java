package com.behavior.sdk.trigger.project.service;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
import com.behavior.sdk.trigger.project.dto.ProjectUpdateRequest;
import com.behavior.sdk.trigger.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse createProject(ProjectCreateRequest request, User user);

    ProjectResponse getProject(UUID projectId);

    List<ProjectResponse> getAllProjects();

    ProjectResponse updateProject(UUID projectId, ProjectUpdateRequest request);

    void deleteProject(UUID projectId);

    List<ProjectResponse> getProjectsByUserId(UUID userId);
}
