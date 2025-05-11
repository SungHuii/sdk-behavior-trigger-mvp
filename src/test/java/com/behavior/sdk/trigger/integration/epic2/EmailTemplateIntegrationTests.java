package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.email_template.dto.EmailTemplateCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmailTemplateIntegrationTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper om;

    private UUID conditionId;
    private UUID templateId;

    @BeforeAll
    void setUpCondition() throws Exception {
        // Create a condition and set conditionId
        String conditionJson = mockMvc.perform(post("/api/conditions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Condition\"}"))
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

        String body = om.writeValueAsString(request);

        String json = mockMvc.perform(post("/api/email-templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.conditionId").value(conditionId.toString()))
                .andExpect(jsonPath("$.subject").value("테스트 이벤트 제목"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        templateId = UUID.fromString(om.readTree(json).get("id").asText());
        assertThat(templateId).isNotNull();
    }
}
