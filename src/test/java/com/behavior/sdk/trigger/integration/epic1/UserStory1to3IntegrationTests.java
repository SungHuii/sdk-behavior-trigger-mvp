package com.behavior.sdk.trigger.integration.epic1;

import com.behavior.sdk.trigger.TriggerApplication;
import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.config.WebConfig;
import com.behavior.sdk.trigger.log_event.dto.LogEventCreateRequest;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import com.behavior.sdk.trigger.user.entity.User;
import com.behavior.sdk.trigger.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TriggerApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestSecurityConfig.class, WebConfig.class})
public class UserStory1to3IntegrationTests {

   private static final Logger log = LoggerFactory.getLogger(UserStory1to3IntegrationTests.class);

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper om;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private ProjectRepository projectRepository;

   private UUID projectId;
   private UUID visitorId;
   private User testUser;

   @BeforeAll
   void setup() {
      projectRepository.deleteAll();
      userRepository.deleteAll();

      UUID userId = UUID.randomUUID();
      testUser = userRepository.save(User.builder()
              .email("test+" + userId + "@example.com")
              .password("encoded-password")
              .build());

      // SecurityContext에 수동으로 인증 객체 주입
      var auth = new UsernamePasswordAuthenticationToken(
              testUser,
              null,
              List.of(new SimpleGrantedAuthority("ROLE_USER")) // 권한이 있다면 명시
      );
      SecurityContextHolder.getContext().setAuthentication(auth);
   }

   @AfterEach
   void tearDown() {
      // SecurityContext 초기화
      SecurityContextHolder.clearContext();
   }


   @Test
   @Order(1)
   @DisplayName("1. 프로젝트 생성 및 projectId 발급")
   void t1_createProject() throws Exception {
      var request = new ProjectCreateRequest("통합 테스트 프로젝트", List.of("https://example.com"));
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
      log.info("생성된 projectId: {}", projectId);
   }

   @Test
   @Order(2)
   @DisplayName("2. Visitor 생성 및 visitorId 발급")
   void t2_createVisitor() throws Exception {
      Assumptions.assumeTrue(projectId != null, "projectId가 null이면 스킵");

      String json = mockMvc.perform(post("/api/visitors")
                      .param("projectId", projectId.toString()))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.id").exists())
              .andReturn()
              .getResponse()
              .getContentAsString();

      visitorId = UUID.fromString(om.readTree(json).get("id").asText());
      log.info("발급된 visitorId: {}", visitorId);
   }

   @Test
   @Order(3)
   @DisplayName("3. page_view 이벤트 전송 후 로그 저장")
   void t3_sendLogEvent() throws Exception {
      Assumptions.assumeTrue(projectId != null, "projectId가 null이면 스킵");
      Assumptions.assumeTrue(visitorId != null, "visitorId가 null이면 스킵");

      var logEventRequest = new LogEventCreateRequest();
      logEventRequest.setEventType(EventType.PAGE_VIEW);
      logEventRequest.setOccurredAt(LocalDateTime.now());
      logEventRequest.setPageUrl("https://example.com");

      log.info("전송할 projectId: {}", projectId);
      log.info("전송할 visitorId: {}", visitorId);

      mockMvc.perform(post("/api/logs")
                      .param("projectId", projectId.toString())
                      .param("visitorId", visitorId.toString())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(om.writeValueAsString(logEventRequest))
                      .header("Origin", "https://example.com"))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.id").exists())
              .andExpect(jsonPath("$.projectId").value(projectId.toString()))
              .andExpect(jsonPath("$.visitorId").value(visitorId.toString()))
              .andExpect(jsonPath("$.pageUrl").value("https://example.com"));
   }
}