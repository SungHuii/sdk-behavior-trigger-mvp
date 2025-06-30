package com.behavior.sdk.trigger.integration.epic1;

import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.log_event.dto.LogEventCreateRequest;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestSecurityConfig.class)
public class UserStory1to3IntegrationTests {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper om;

   private UUID projectId;
   private UUID visitorId;

   @Test
   @Order(1)
   @DisplayName("1. 프로젝트 생성 및 projectId 발급")
   void t1_createProject() throws Exception {
      var request = new ProjectCreateRequest("통합 테스트 프로젝트", "https://example.com");
      String body = om.writeValueAsString(request);

      String json = mockMvc.perform(post("/api/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

      projectId = UUID.fromString(om.readTree(json).get("id").asText());

   }

   @Test
   @Order(2)
   @DisplayName("2. Visitor 생성 및 visitorId 발급")
   void t2_createVisitor() throws Exception {
      // 1번 테스트에서 발급된 projectKey 사용
      String json = mockMvc.perform(post("/api/visitors")
                  .param("projectId", projectId.toString()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

      visitorId = UUID.fromString(om.readTree(json).get("id").asText());
   }

   @Test
   @Order(3)
   @DisplayName("3. page_view 이벤트 전송 후 로그 저장")
   void t3_sendLogEvent() throws Exception {
      var logEventRequest = new LogEventCreateRequest();
      logEventRequest.setEventType(EventType.PAGE_VIEW);
      logEventRequest.setOccurredAt(LocalDateTime.now());
      logEventRequest.setPageUrl("https://example.com");

      mockMvc.perform(post("/api/logs")
                  .param("projectId", projectId.toString())
                  .param("visitorId", visitorId.toString())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(om.writeValueAsString(logEventRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.projectId").value(projectId.toString()))
            .andExpect(jsonPath("$.visitorId").value(visitorId.toString()))
            .andExpect(jsonPath("$.pageUrl").value("https://example.com"));
   }

}
