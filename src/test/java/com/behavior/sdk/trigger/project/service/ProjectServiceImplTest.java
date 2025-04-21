package com.behavior.sdk.trigger.project.service;

import com.behavior.sdk.trigger.project.dto.ProjectCreateRequest;
import com.behavior.sdk.trigger.project.dto.ProjectResponse;
import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectServiceImpl;

    private UUID ownerId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
    }

    @Test
    void createProjectAndReturnDto() {
        ProjectCreateRequest request = new ProjectCreateRequest("Test Project");
        Project savedProject = Project.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .sdkKey("test-sdk-key")
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .build();

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponse responseDto = projectServiceImpl.createProject(ownerId, request);

        assertThat(responseDto.getId()).isEqualTo(savedProject.getId());
        assertThat(responseDto.getName()).isEqualTo(savedProject.getName());
        assertThat(responseDto.getSdkKey()).isEqualTo(savedProject.getSdkKey());
        assertThat(responseDto.getOwnerId()).isEqualTo(savedProject.getOwnerId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }
}