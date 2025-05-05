package com.behavior.sdk.trigger.project.service;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectResponse createProject(ProjectCreateRequest request);

    ProjectResponse getProject(UUID projectId);

    List<ProjectResponse> getAllProjects();

    void deleteProject(UUID projectId);
}
