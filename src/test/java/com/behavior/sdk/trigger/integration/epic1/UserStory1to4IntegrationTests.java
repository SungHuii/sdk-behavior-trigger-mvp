package com.behavior.sdk.trigger.integration.epic1;

import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.log_event.dto.LogEventCreateRequest;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.behavior.sdk.trigger.user.entity.User;
import com.behavior.sdk.trigger.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
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
public class UserStory1to4IntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    private UUID projectId;
    private UUID visitorId;
    private User testUser;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setupProjectAndVisitor() throws Exception {

        UUID userId = UUID.randomUUID();
        testUser = userRepository.save(User.builder()
                .email("test+" + userId + "@example.com")
                .password("encoded-password")
                .build());

        var auth = new UsernamePasswordAuthenticationToken(
                testUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        String projectJson = mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"유저스토리 1-4 통합 테스트 프로젝트\", \"domain\": \"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        projectId = UUID.fromString(om.readTree(projectJson).get("id").asText());

        String visitorJson = mockMvc.perform(post("/api/visitors")
                .param("projectId", projectId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        visitorId = UUID.fromString(om.readTree(visitorJson).get("id").asText());
    }

    @Test
    @Order(1)
    @DisplayName("1. pageUrl을 포함한 로그 생성 및 저장")
    void t4_createLogWithPageUrl() throws Exception {

        LogEventCreateRequest request = new LogEventCreateRequest();
        request.setEventType(EventType.PAGE_VIEW);
        request.setOccurredAt(LocalDateTime.now());
        request.setPageUrl("https://example.com");

        mockMvc.perform(post("/api/logs")
                .param("projectId", projectId.toString())
                .param("visitorId", visitorId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Origin", "https://example.com")
                .content(om.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId").value(projectId.toString()))
                .andExpect(jsonPath("$.visitorId").value(visitorId.toString()))
                .andExpect(jsonPath("$.pageUrl").value("https://example.com"))
                .andExpect(jsonPath("$.eventType").value("page_view"));
    }
}
