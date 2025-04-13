package com.behavior.sdk.trigger.project.controller;

import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

   private final ProjectRepository projectRepository;

   @PostMapping
   public Project createProject(@RequestBody Project request) {
      return projectRepository.save(request);
   }

   @GetMapping
   public List<Project> getAllProjects() {
      return projectRepository.findAll();
   }
}
