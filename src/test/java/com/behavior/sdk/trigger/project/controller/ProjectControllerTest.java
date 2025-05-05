package com.behavior.sdk.trigger.project.controller;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
import com.behavior.sdk.trigger.project.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @MockitoBean
   private ProjectService service;

   @Autowired
   private ObjectMapper om;

   @Test
   @DisplayName("POST /api/projects - 프로젝트 생성")
   void createProject() throws Exception {

      var dto = ProjectResponse.builder()
              .id(UUID.randomUUID())
              .name("테스트 프로젝트")
              .createdAt(LocalDateTime.now())
              .build();

      given(service.createProject(any(ProjectCreateRequest.class))).willReturn(dto);

      mockMvc.perform(post("/api/projects")
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON)
                  .content(om.writeValueAsString(new ProjectCreateRequest("테스트 프로젝트"))))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(dto.getId().toString()))
            .andExpect(jsonPath("$.name").value("테스트 프로젝트"))
            .andExpect(jsonPath("$.createdAt").exists());

   }

   @Test
   @DisplayName("GET /api/projects/allProjects - 전체 프로젝트 조회")
   void listProjects() throws Exception {
      var project1 = ProjectResponse.builder()
              .id(UUID.randomUUID()).name("프로젝트1").createdAt(LocalDateTime.now()).build();
      var project2 = ProjectResponse.builder()
              .id(UUID.randomUUID()).name("프로젝트2").createdAt(LocalDateTime.now()).build();

      given(service.getAllProjects()).willReturn(List.of(project1, project2));

      mockMvc.perform(get("/api/projects/allProjects"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$[0].name").value("프로젝트1"))
              .andExpect(jsonPath("$", hasSize(2)));
   }

   @Test
   @DisplayName("DELETE /api/projects/{projectId} - 특정 프로젝트 삭제")
   void deleteProject() throws Exception {
        UUID projectId = UUID.randomUUID();

        mockMvc.perform(delete("/api/projects/{projectId}", projectId))
                .andExpect(status().isNoContent());
      BDDMockito.then(service).should().deleteProject(projectId);
   }
}