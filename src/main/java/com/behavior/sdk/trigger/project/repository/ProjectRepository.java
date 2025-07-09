package com.behavior.sdk.trigger.project.repository;

import com.behavior.sdk.trigger.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
  
   List<Project> findAllByDeletedAtIsNull();

   Optional<Project> findByIdAndDeletedAtIsNull(UUID id);

   List<Project> findAllByUserIdAndDeletedAtIsNull(UUID userId);
}

