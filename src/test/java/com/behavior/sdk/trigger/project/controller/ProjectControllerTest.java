package com.behavior.sdk.trigger.project.controller;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
import com.behavior.sdk.trigger.project.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

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
   void createProject_returnsCreated() throws Exception {
      // Given
      UUID ownerId = UUID.randomUUID();
      var request = new ProjectCreateRequest("Test Project");
      var response = ProjectResponse.builder()
            .id(UUID.randomUUID())
            .name("Test Project")
            .sdkKey("test-sdk-key")
            .ownerId(ownerId)
            .createdAt(LocalDateTime.now())
            .build();

      given(service.createProject(eq(ownerId), any(ProjectCreateRequest.class))).willReturn(response);

      mockMvc.perform(post("/api/projects")
                  .param("ownerId", ownerId.toString())
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON)
                  .content(om.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(response.getId().toString()))
            .andExpect(jsonPath("$.name").value("Test Project"));

   }

   @Test
   void listProjects_returnsOk() throws Exception {
      UUID ownerId = UUID.randomUUID();
      ProjectResponse response = ProjectResponse.builder()
            .id(UUID.randomUUID())
            .name("Project 1")
            .sdkKey("test key1")
            .ownerId(ownerId)
            .createdAt(LocalDateTime.now())
            .build();
      given(service.getProjectsByOwner(ownerId)).willReturn(Collections.singletonList(response));

      mockMvc.perform(get("/api/projects").param("ownerId", ownerId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(response.getId().toString()));
   }

   @Test
   void deleteProject_returnsNoContent() throws Exception {
      UUID projectId = UUID.randomUUID();
      mockMvc.perform(delete("/api/projects/{id}", projectId))
            .andExpect(status().isNoContent());
   }
}