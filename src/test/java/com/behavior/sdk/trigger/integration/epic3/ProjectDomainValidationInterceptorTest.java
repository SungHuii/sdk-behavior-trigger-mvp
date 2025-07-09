package com.behavior.sdk.trigger.integration.epic3;

import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class ProjectDomainValidationInterceptorTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProjectRepository projectRepository;

    private UUID validProjectId;

    @BeforeEach
    void setUp() {

        Project project = Project.builder()
                .name("Test Project")
                .allowedDomains(List.of("https://valid-domain.com"))
                .build();
        projectRepository.deleteAll();
        validProjectId = projectRepository.save(project).getId();
    }

    @Test
    void shouldReturn400IfProjectIdIsMissing() throws Exception {
        mockMvc.perform(post("/api/logs"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400IfProjectIdIsInvalid() throws Exception {
        mockMvc.perform(post("/api/logs")
                .param("projectId", "invalid-uuid-or-not-a-uuid"))
                .andExpect(status().isBadRequest());
    }
}
