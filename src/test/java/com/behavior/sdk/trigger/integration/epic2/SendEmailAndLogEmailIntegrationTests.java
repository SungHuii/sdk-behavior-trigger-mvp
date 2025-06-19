package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SendEmailAndLogEmailIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private EmailLogRepository emailLogRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    private UUID projectId;
    private UUID visitorId;
    private UUID conditionId;
    private UUID templateId;

    @BeforeAll
    void setup() throws Exception {

        emailLogRepository.deleteAll();

        String project = mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Email Test Project\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        projectId = UUID.fromString(om.readTree(project).get("id").asText());

        String visitor = mockMvc.perform(post("/api/visitors")
                .param("projectId", projectId.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        visitorId = UUID.fromString(om.readTree(visitor).get("id").asText());

        mockMvc.perform(post("/api/visitors/{visitorId}/email", visitorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk());

        Map<String, Object> condition = Map.of(
                "projectId", projectId.toString(),
                "eventType", "page_view",
                "operator", "GREATER_THAN",
                "threshold", 30,
                "pageUrl", "https://example.com"
        );

        String createCondition = mockMvc.perform(post("/api/conditions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(condition)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        conditionId = UUID.fromString(om.readTree(createCondition).get("id").asText());

        Map<String, Object> template = Map.of(
                "conditionId", conditionId.toString(),
                "subject", "테스트 이메일입니다.",
                "body", "테스트 이메일 본문입니다."
        );

        String createTemplate = mockMvc.perform(post("/api/email-templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(template)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        templateId = UUID.fromString(om.readTree(createTemplate).get("id").asText());

        doNothing().when(mailSender).send((SimpleMailMessage) any());
    }

    @Test
    @Order(1)
    @DisplayName("이메일 전송 및 로그 저장")
    void t1_sendEmail_and_logEmail() throws Exception {

        EmailSendRequest sendRequest = EmailSendRequest.builder()
                .visitorId(visitorId)
//                .templateId(templateId)
                .build();
        String body = om.writeValueAsString(sendRequest);

        String response = mockMvc.perform(post("/api/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.logId").exists())
                .andExpect(jsonPath("$.status").value("SENT"))
                .andReturn().getResponse().getContentAsString();

        UUID logId = UUID.fromString(om.readTree(response).get("logId").asText());

        EmailLog emailLog = emailLogRepository.findById(logId).orElseThrow();
        assertThat(emailLog.getVisitorId()).isEqualTo(visitorId);
//        assertThat(emailLog.getTemplateId()).isEqualTo(templateId);
        assertThat(emailLog.getStatus()).isEqualTo(EmailStatus.SENT);
    }

    @Test
    @Order(2)
    @DisplayName("이메일 로그 조회")
    void t2_getEmailLogs() throws Exception {
        mockMvc.perform(get("/api/email-logs")
                .param("visitorId", visitorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].visitorId").value(visitorId.toString()))
//                .andExpect(jsonPath("$[0].templateId").value(templateId.toString()))
                .andExpect(jsonPath("$[0].status").value(EmailStatus.SENT.toString()))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @Order(3)
    @DisplayName("이메일 로그 삭제")
    void t3_softDeleteEmailLog() throws Exception {
        String logId = emailLogRepository.findAll().get(0).getId().toString();

        mockMvc.perform(delete("/api/email-logs/{logId}", logId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/email-logs")
                .param("visitorId", visitorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
