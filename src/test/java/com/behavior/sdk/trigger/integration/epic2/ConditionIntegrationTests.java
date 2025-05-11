package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.condition.dto.ConditionCreateRequest;
import com.behavior.sdk.trigger.condition.dto.ConditionResponse;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConditionIntegrationTests {

   @Autowired
   private MockMvc mockMvc;
   @Autowired
   private ObjectMapper om;

   private UUID projectId;
   private UUID conditionId;

   @BeforeAll
   void setUpProject() throws Exception {

      String projectJson = mockMvc.perform(post("/api/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"조건 테스트용 프로젝트\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andReturn().getResponse().getContentAsString();

      projectId = UUID.fromString(om.readTree(projectJson).get("id").asText());
   }

   @Test
   @Order(1)
   @DisplayName("1. Condition 생성")
   void t1_createCondition() throws Exception {
      ConditionCreateRequest conditionCreateRequest = ConditionCreateRequest.builder()
            .projectId(projectId)
            .eventType(EventType.PAGE_VIEW)
            .operator("GRATER_THAN")
            .threshold(60)
            .pageUrl("https://example.com/event")
            .build();

      String payload = om.writeValueAsString(conditionCreateRequest);

      String json = mockMvc.perform(post("/api/conditions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.projectId").value(projectId.toString()))
            .andExpect(jsonPath("$.eventType").value("page_view"))
            .andExpect(jsonPath("$.operator").value("GRATER_THAN"))
            .andExpect(jsonPath("$.threshold").value(60))
            .andExpect(jsonPath("$.pageUrl").value("https://example.com/event"))
            .andReturn().getResponse().getContentAsString();

      conditionId = UUID.fromString(om.readTree(json).get("id").asText());
      assertThat(conditionId).isNotNull();
   }

   @Test
   @Order(2)
   @DisplayName("2. Condition 목록 조회")
   void t2_listConditions() throws Exception {

      String json = mockMvc.perform(get("/api/conditions/{projectId}", projectId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.hasSize(1)))
            .andReturn().getResponse().getContentAsString();

      List<ConditionResponse> list = om.readValue(json, om.getTypeFactory().constructCollectionType(List.class, ConditionResponse.class));
      assertThat(list.get(0).getId()).isEqualTo(conditionId);
   }

   @Test
   @Order(3)
   @DisplayName("3. Condition soft delete")
   void t3_softDeleteCondition() throws Exception {
      mockMvc.perform(delete("/api/conditions/{conditionId}", conditionId))
            .andExpect(status().isNoContent());

      mockMvc.perform(get("/api/conditions/{projectId}", projectId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.hasSize(0)));
   }
}
