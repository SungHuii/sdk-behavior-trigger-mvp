package com.behavior.sdk.trigger.project.controller;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
import com.behavior.sdk.trigger.project.service.ProjectService;
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
public class ProjectController {

   private final ProjectService projectService;

   @PostMapping
   public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectCreateRequest request,
                                                        @RequestParam UUID ownerId) {
      ProjectResponse created = projectService.createProject(ownerId, request);
      return ResponseEntity.status(HttpStatus.CREATED).body(created);
   }

   @GetMapping("/{projectId}")
   public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId) {
      return ResponseEntity.ok(projectService.getProject(projectId));
   }

   @GetMapping
   public ResponseEntity<List<ProjectResponse>> listProjects(@RequestParam UUID ownerId) {
      List<ProjectResponse> projects = projectService.getProjectsByOwner(ownerId);
      return ResponseEntity.ok(projects);
   }

   @DeleteMapping("/{projectId}")
   public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
      projectService.deleteProject(projectId);
      return ResponseEntity.noContent().build();
   }

}
