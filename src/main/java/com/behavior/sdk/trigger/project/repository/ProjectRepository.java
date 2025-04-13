package com.behavior.sdk.trigger.project.repository;

import com.behavior.sdk.trigger.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

   boolean existsByApiKey(String apiKey);
}
