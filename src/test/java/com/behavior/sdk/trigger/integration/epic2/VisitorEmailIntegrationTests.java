package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.user.entity.User;
import com.behavior.sdk.trigger.user.repository.UserRepository;
import com.behavior.sdk.trigger.visitor.dto.VisitorEmailRequest;
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
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VisitorEmailIntegrationTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper om;
    @Autowired UserRepository userRepository;

    private UsernamePasswordAuthenticationToken auth;

    @BeforeAll
    void setup() {
        User testUser = userRepository.saveAndFlush(User.builder()
                .email("test@example.com")
                .password("pw")
                .build());

        auth = new UsernamePasswordAuthenticationToken(
                testUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("방문자 이메일 등록 API")
    void t_updateEmail() throws Exception {

        String projectJson = mockMvc.perform(post("/api/projects")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"테스트 프로젝트\", \"allowedDomains\":[\"https://example.com\"]}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID projectId = UUID.fromString(om.readTree(projectJson).get("id").asText());

        String visitorJson = mockMvc.perform(post("/api/visitors")
                        .param("projectId", projectId.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID visitorId = UUID.fromString(om.readTree(visitorJson).get("id").asText());

        VisitorEmailRequest req = new VisitorEmailRequest();
        req.setEmail("tester@example.com");

        mockMvc.perform(post("/api/visitors/{visitorId}/email", visitorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
