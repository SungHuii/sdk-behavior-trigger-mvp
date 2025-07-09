package com.behavior.sdk.trigger.integration.epic3;

import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import com.behavior.sdk.trigger.user.entity.User;
import com.behavior.sdk.trigger.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectDomainValidationInterceptorTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    private UUID validProjectId;

    @BeforeAll
    void setUp() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
        User testUser = userRepository.save(User.builder()
                .email("segment-test@example.com")
                .password("encoded-password")
                .build());

        var auth = new UsernamePasswordAuthenticationToken(
                testUser, // Principal
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        Project project = Project.builder()
                .name("Test Project")
                .allowedDomains(List.of("https://valid-domain.com"))
                .user(testUser)
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
