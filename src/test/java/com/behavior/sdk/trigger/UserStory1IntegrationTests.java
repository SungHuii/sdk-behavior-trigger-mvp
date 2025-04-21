package com.behavior.sdk.trigger;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserStory1IntegrationTests {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper om;

   private String projectKey;
   private String visitorKey;

   @Test
   @Order(1)
   @DisplayName("1. 프로젝트 생성 후 SDK Key 발급")
   void t1_createProject() throws Exception {
      ProjectCreateRequest request = new ProjectCreateRequest("통합 테스트 프로젝트");

      String body = om.writeValueAsString(request);

      String ownerId = UUID.randomUUID().toString();

      String json = mockMvc.perform(post("/api/projects")
            .param("ownerId", ownerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.sdkKey").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

      projectKey = om.readTree(json).get("sdkKey").asText();

   }

   @Test
   @Order(2)
   @DisplayName("2. Visitor 생성 (SDK 삽입 후 첫 진입)")
   void t2_createVisitor() throws Exception {
      // 1번 테스트에서 발급된 projectKey 사용
      String json = mockMvc.perform(post("/api/visitors")
                  .param("projectKey", projectKey))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.visitorKey").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

      visitorKey = om.readTree(json).get("visitorKey").asText();
   }

   @Test
   @Order(3)
   @DisplayName("3. 세션/로그 이벤트 생성 흐름")
   void t3_sendLogEvent() throws Exception {
      mockMvc.perform(post("/api/logs")
                  .param("projectKey", projectKey)
                  .param("visitorKey", visitorKey)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{ \"eventType\":\"page_view\", \"eventData\":{} }"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.projectId").exists());
   }

}
