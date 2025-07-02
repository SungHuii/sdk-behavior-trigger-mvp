package com.behavior.sdk.trigger.integration.epic1;

import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.log_event.dto.LogEventCreateRequest;
import com.behavior.sdk.trigger.log_event.enums.EventType;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestSecurityConfig.class)
public class UserStory1to5IntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    private UUID projectId;
    private UUID visitorA;
    private UUID visitorB;
    LocalDateTime now = LocalDateTime.now();

    @BeforeAll
    void setup() throws Exception {
        String projectJson = mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"유저스토리 1-5 통합 테스트 프로젝트\", \"domain\": \"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        projectId = UUID.fromString(om.readTree(projectJson).get("id").asText());

        String visitorAJson = mockMvc.perform(post("/api/visitors")
                .param("projectId", projectId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        visitorA = UUID.fromString(om.readTree(visitorAJson).get("id").asText());

        String visitorBJson = mockMvc.perform(post("/api/visitors")
                .param("projectId", projectId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        visitorB = UUID.fromString(om.readTree(visitorBJson).get("id").asText());

        // visitorA 로그 2건 생성
        LogEventCreateRequest requestA = new LogEventCreateRequest();
        requestA.setEventType(EventType.PAGE_VIEW);
        requestA.setOccurredAt(now);
        requestA.setPageUrl("https://example.com/pageA");
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/api/logs")
                    .param("projectId", projectId.toString())
                    .param("visitorId", visitorA.toString())
                    .header("Origin", "https://example.com")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsString(requestA)))
                    .andExpect(status().isCreated());
        }

        // visitorB 로그 1건 생성
        LogEventCreateRequest requestB = new LogEventCreateRequest();
        requestB.setEventType(EventType.PAGE_VIEW);
        requestB.setOccurredAt(now);
        requestB.setPageUrl("https://example.com/pageB");
        mockMvc.perform(post("/api/logs")
                .param("projectId", projectId.toString())
                .param("visitorId", visitorB.toString())
                .header("Origin", "https://example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(requestB)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("5. 프로젝트별 로그 조회")
    void t5_listByProject() throws Exception {
        mockMvc.perform(get("/api/logs")
                .param("projectId", projectId.toString())
                        .header("Origin", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("6. 방문자별 로그 조회")
    void t6_listByVisitor() throws Exception {
        mockMvc.perform(get("/api/logs")
                .param("projectId", projectId.toString())
                .param("visitorId", visitorA.toString())
                        .header("Origin", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }
}
