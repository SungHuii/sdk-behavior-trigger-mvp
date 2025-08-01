package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.condition.dto.ConditionCreateRequest;
import com.behavior.sdk.trigger.condition.dto.ConditionResponse;
import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import com.behavior.sdk.trigger.user.entity.User;
import com.behavior.sdk.trigger.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestSecurityConfig.class)
public class ConditionIntegrationTests {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper om;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private ProjectRepository projectRepository;

   private UUID projectId;
   private UUID conditionId;
   private UsernamePasswordAuthenticationToken auth;

   @BeforeAll
   void setUp() {
      UUID userId = UUID.randomUUID();

      User testUser = userRepository.saveAndFlush(User.builder()
              .email("test+" + userId + "@example.com")
              .password("encoded-password")
              .build());

      auth = new UsernamePasswordAuthenticationToken(
              testUser,
              null,
              List.of(new SimpleGrantedAuthority("ROLE_USER"))
      );

      Project project = Project.builder()
              .name("조건 테스트용 프로젝트")
              .user(testUser)
              .allowedDomains(List.of("https://example.com"))
              .build();

      projectId = projectRepository.saveAndFlush(project).getId();
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
                      .with(authentication(auth)) // 인증 추가
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
      String json = mockMvc.perform(get("/api/conditions/{projectId}", projectId)
                      .with(authentication(auth))) // 인증 추가
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
      mockMvc.perform(delete("/api/conditions/{conditionId}", conditionId)
                      .with(authentication(auth))) // 인증 추가
              .andExpect(status().isNoContent());

      mockMvc.perform(get("/api/conditions/{projectId}", projectId)
                      .with(authentication(auth))) // 인증 추가
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", Matchers.hasSize(0)));
   }
}
