package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.visitor.dto.VisitorEmailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class VisitorEmailIntegrationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @Test
    @DisplayName("방문자가 구독 폼에 이메일 입력 후, 이메일 업데이트 API 호출, 저장")
    void t_updateEmail() throws Exception {

        // Create Project
        String projectJson = mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Project\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID projectId = UUID.fromString(om.readTree(projectJson).get("id").asText());

        // Create Visitor
        String visitorJson = mockMvc.perform(post("/api/visitors")
                .param("projectId", projectId.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID visitorId = UUID.fromString(om.readTree(visitorJson).get("id").asText());

        // Update Email
        VisitorEmailRequest request = new VisitorEmailRequest();
        request.setEmail("test@example.com");

        mockMvc.perform(post("/api/visitors/{visitorId}/email", visitorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
