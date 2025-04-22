package com.behavior.sdk.trigger.project.service;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse createProject(UUID ownerId, ProjectCreateRequest request);

    ProjectResponse getProject(UUID projectId);

    List<ProjectResponse> getProjectsByOwner(UUID ownerId);

    List<ProjectResponse> getAllProjects();

    void deleteProject(UUID projectId);
}
