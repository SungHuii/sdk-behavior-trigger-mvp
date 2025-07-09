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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
public class EmailTemplateIntegrationTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper om;
    @Autowired UserRepository userRepository;

    private UUID projectId;
    private UUID conditionId;
    private UUID templateId;
    private UsernamePasswordAuthenticationToken auth;

    @BeforeAll
    void setUpCondition() throws Exception {
        User testUser = userRepository.saveAndFlush(User.builder()
                .email("segment-test@example.com")
                .password("encoded-password")
                .build());

        auth = new UsernamePasswordAuthenticationToken(
                testUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String projectJson = mockMvc.perform(post("/api/projects")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"조건 테스트용 프로젝트\", \"allowedDomains\":[\"https://example.com\"]}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        projectId = UUID.fromString(om.readTree(projectJson).get("id").asText());

        Map<String, Object> conditionRequest = Map.of(
                "projectId", projectId.toString(),
                "eventType", "page_view",
                "operator", "GREATER_THAN",
                "threshold", 60,
                "pageUrl", "https://example.com/event"
        );
        String conditionJson = mockMvc.perform(post("/api/conditions")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(conditionRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        conditionId = UUID.fromString(om.readTree(conditionJson).get("id").asText());
    }

    @Test
    @Order(1)
    @DisplayName("1. 이메일 템플릿 생성")
    void t1_createEmailTemplate() throws Exception {
        EmailTemplateCreateRequest request = EmailTemplateCreateRequest.builder()
                .conditionId(conditionId)
                .subject("테스트 이벤트 제목")
                .body("안녕하세요, {{visitorName}}님! 테스트 이벤트에 초대합니다.")
                .build();

        String json = mockMvc.perform(post("/api/email-templates")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.conditionId").value(conditionId.toString()))
                .andExpect(jsonPath("$.subject").value("테스트 이벤트 제목"))
                .andReturn().getResponse().getContentAsString();

        templateId = UUID.fromString(om.readTree(json).get("id").asText());
        assertThat(templateId).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("2. 이메일 템플릿 목록 조회")
    void t2_listEmailTemplates() throws Exception {
        mockMvc.perform(get("/api/email-templates/{conditionId}", conditionId)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(templateId.toString()));
    }

    @Test
    @Order(3)
    @DisplayName("3. 이메일 템플릿 삭제(soft delete)")
    void t3_softDeleteEmailTemplate() throws Exception {
        mockMvc.perform(delete("/api/email-templates/{templateId}", templateId)
                        .with(authentication(auth)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/email-templates/{conditionId}", conditionId)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}