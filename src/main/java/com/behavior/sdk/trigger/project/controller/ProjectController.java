package com.behavior.sdk.trigger.project.controller;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
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
   @Operation(summary = "프로젝트 생성", description = "OwnerId와 프로젝트 이름으로 프로젝트를 생성합니다.")
   @ApiResponses({
         @ApiResponse(responseCode = "201", description = "프로젝트 생성 성공",
         content=@Content(schema = @Schema(implementation = ProjectResponse.class)))
   })
   public ResponseEntity<ProjectResponse> createProject(@Parameter(description = "요청 body") @RequestBody @Valid ProjectCreateRequest request,
                                                        @Parameter(description = "소유자 ID") @RequestParam UUID ownerId) {
      ProjectResponse created = projectService.createProject(ownerId, request);
      return ResponseEntity.status(HttpStatus.CREATED).body(created);
   }

   @Operation(summary = "특정 프로젝트 조회", description = "프로젝트 ID로 프로젝트를 조회합니다.")
   @GetMapping("/{projectId}")
   @ApiResponses({
         @ApiResponse(responseCode = "200", description = "프로젝트 조회 성공",
         content=@Content(schema = @Schema(implementation = ProjectResponse.class))),
         @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
   })
   public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId) {
      return ResponseEntity.ok(projectService.getProject(projectId));
   }

   @Operation(summary = "모든 프로젝트 조회", description = "모든 프로젝트를 조회합니다.")
   @GetMapping
   @ApiResponses({
         @ApiResponse(responseCode = "200", description = "모든 프로젝트 조회 성공",
         content=@Content(schema = @Schema(implementation = ProjectResponse.class)))
   })
   public ResponseEntity<List<ProjectResponse>> listProjects(@RequestParam UUID ownerId) {
      List<ProjectResponse> projects = projectService.getProjectsByOwner(ownerId);
      return ResponseEntity.ok(projects);
   }

   @Operation(summary = "특정 프로젝트 삭제", description = "ProjectId로 조회된 프로젝트를 삭제합니다.")
   @DeleteMapping("/{projectId}")
   @ApiResponses({
         @ApiResponse(responseCode = "204", description = "프로젝트 삭제 성공"),
         @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음")
   })
   public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
      projectService.deleteProject(projectId);
      return ResponseEntity.noContent().build();
   }

}
