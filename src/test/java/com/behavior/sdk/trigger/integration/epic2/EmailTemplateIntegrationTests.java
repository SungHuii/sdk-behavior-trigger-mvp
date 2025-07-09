package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.email_template.dto.EmailTemplateCreateRequest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestSecurityConfig.class)
public class EmailTemplateIntegrationTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper om;
    @Autowired UserRepository userRepository;

    private UsernamePasswordAuthenticationToken auth;
    private UUID projectId;
    private UUID conditionId;
    private UUID templateId;

    @BeforeAll
    void setUp() throws Exception {
        User user = userRepository.saveAndFlush(User.builder()
                .email("segment-test@example.com")
                .password("encoded")
                .build());

        auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Create Project
        String projectJson = mockMvc.perform(post("/api/projects")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"조건 테스트용 프로젝트\", \"allowedDomains\":[\"https://example.com\"]}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        projectId = UUID.fromString(om.readTree(projectJson).get("id").asText());

        // Create Condition
        Map<String, Object> req = Map.of(
                "projectId", projectId.toString(),
                "eventType", "page_view",
                "operator", "GREATER_THAN",
                "threshold", 60,
                "pageUrl", "https://example.com/event"
        );
        String conditionJson = mockMvc.perform(post("/api/conditions")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        conditionId = UUID.fromString(om.readTree(conditionJson).get("id").asText());
    }

    @Test
    @Order(1)
    void t1_create() throws Exception {
        EmailTemplateCreateRequest request = EmailTemplateCreateRequest.builder()
                .conditionId(conditionId)
                .subject("테스트 제목")
                .body("안녕하세요 {{visitorName}}님")
                .build();

        String json = mockMvc.perform(post("/api/email-templates")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conditionId").value(conditionId.toString()))
                .andReturn().getResponse().getContentAsString();
        templateId = UUID.fromString(om.readTree(json).get("id").asText());
        assertThat(templateId).isNotNull();
    }

    @Test
    @Order(2)
    void t2_list() throws Exception {
        mockMvc.perform(get("/api/email-templates/{conditionId}", conditionId)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(3)
    void t3_delete() throws Exception {
        mockMvc.perform(delete("/api/email-templates/{templateId}", templateId)
                        .with(authentication(auth)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/email-templates/{conditionId}", conditionId)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
