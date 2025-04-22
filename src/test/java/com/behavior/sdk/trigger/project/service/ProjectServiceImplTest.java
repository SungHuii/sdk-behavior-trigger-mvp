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

    @Test
    void getProject_existing_returnDto() {
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder()
              .id(projectId)
              .name("Test Project")
              .sdkKey("Test-key")
              .ownerId(ownerId)
              .createdAt(LocalDateTime.now())
              .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectResponse dto = projectServiceImpl.getProject(projectId);

        assertThat(dto.getId()).isEqualTo(project.getId());
        assertThat(dto.getName()).isEqualTo(project.getName());
    }

    @Test
    void getProject_notFound_throwsException() {
        UUID projectid = UUID.randomUUID();

        when(projectRepository.findById(projectid)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> projectServiceImpl.getProject(projectid));
    }

    @Test
    void deleteProject_softDelete() {
        UUID projectId = UUID.randomUUID();
        Project project = Project.builder()
              .id(projectId)
              .name("Deleted Project")
              .ownerId(ownerId)
              .createdAt(LocalDateTime.now())
              .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectServiceImpl.deleteProject(projectId);

        assertThat(project.getDeletedAt()).isNotNull();
        verify(projectRepository).save(project);
    }

    @Test
    void getProjectsByOwnerId_filtered_Deleted() {
        Project project1 = Project.builder().ownerId(ownerId).deletedAt(null).build();
        Project project2 = Project.builder().ownerId(ownerId).deletedAt(LocalDateTime.now()).build();

        when(projectRepository.findAllByOwnerIdAndDeletedAtIsNull(ownerId)).thenReturn(List.of(project1));

        List<ProjectResponse> list = projectServiceImpl.getProjectsByOwner(ownerId);

        assertThat(list).hasSize(1);
    }


}