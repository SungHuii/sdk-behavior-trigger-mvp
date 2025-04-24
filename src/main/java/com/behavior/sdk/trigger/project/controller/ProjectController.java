package com.behavior.sdk.trigger.project.controller;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "프로젝트 관리 API")
public class ProjectController {

   private final ProjectService projectService;

   @PostMapping
   public ResponseEntity<ProjectResponse> createProject (
           @RequestBody @Valid ProjectCreateRequest request) {
      ProjectResponse createdProject = projectService.createProject(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
   }

   @GetMapping("/{projectId}")
   public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId) {
      ProjectResponse projectResponse = projectService.getProject(projectId);
      return ResponseEntity.status(HttpStatus.OK).body(projectResponse);
   }

   @GetMapping("/allProjects")
   public ResponseEntity<List<ProjectResponse>> getAllProjects() {
      List<ProjectResponse> projects = projectService.getAllProjects();
      return ResponseEntity.status(HttpStatus.OK).body(projects);
   }

   @DeleteMapping("/{projectId}")
   public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
      projectService.deleteProject(projectId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }



}
