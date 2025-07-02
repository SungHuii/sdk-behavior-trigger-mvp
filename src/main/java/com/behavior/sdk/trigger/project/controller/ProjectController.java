package com.behavior.sdk.trigger.project.controller;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
import com.behavior.sdk.trigger.project.dto.ProjectUpdateRequest;
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

   @ApiResponses({
           @ApiResponse(responseCode = "201", description = "프로젝트 생성 성공",
                   content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
           @ApiResponse(responseCode = "400", description = "잘못된 요청")
   })
   @PostMapping
   @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성하고 SDK 키를 발급합니다.")
   public ResponseEntity<ProjectResponse> createProject (
           @RequestBody @Valid ProjectCreateRequest request) {
      ProjectResponse createdProject = projectService.createProject(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
   }

   @Operation(summary = "프로젝트 조회", description = "프로젝트 ID로 특정 프로젝트 정보를 조회합니다.")
   @ApiResponses({
              @ApiResponse(responseCode = "200", description = "프로젝트 조회 성공",
                     content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
              @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
   })
   @GetMapping("/{projectId}")
   public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId) {
      ProjectResponse projectResponse = projectService.getProject(projectId);
      return ResponseEntity.status(HttpStatus.OK).body(projectResponse);
   }

   @Operation(summary = "모든 프로젝트 조회", description = "모든 프로젝트 정보를 조회합니다.")
    @ApiResponses({
              @ApiResponse(responseCode = "200", description = "모든 프로젝트 조회 성공",
                     content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
              @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    })
   @GetMapping("/allProjects")
   public ResponseEntity<List<ProjectResponse>> getAllProjects() {
      List<ProjectResponse> projects = projectService.getAllProjects();
      return ResponseEntity.status(HttpStatus.OK).body(projects);
   }

   @Operation(summary = "프로젝트 삭제", description = "프로젝트 ID로 특정 프로젝트를 soft-delete 처리해서 삭제합니다.")
   @ApiResponses({
           @ApiResponse(responseCode = "204", description = "프로젝트 삭제 성공"),
           @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
   })
   @DeleteMapping("/{projectId}")
   public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
      projectService.deleteProject(projectId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

   @Operation(summary = "프로젝트 수정", description = "프로젝트 ID로 특정 프로젝트 정보를 수정합니다.")
    @ApiResponses({
              @ApiResponse(responseCode = "200", description = "프로젝트 수정 성공",
                     content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
              @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
    })
    @PutMapping("/{projectId}")
   public ResponseEntity<ProjectResponse> updateProject(
           @PathVariable UUID projectId,
           @RequestBody @Valid ProjectUpdateRequest request) {
      ProjectResponse updatedProject = projectService.updateProject(projectId, request);
      return ResponseEntity.ok(updatedProject);
   }
   
}
