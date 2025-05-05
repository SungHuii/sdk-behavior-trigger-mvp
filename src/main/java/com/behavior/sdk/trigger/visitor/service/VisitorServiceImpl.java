package com.behavior.sdk.trigger.visitor.service;

import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import com.behavior.sdk.trigger.visitor.dto.VisitorResponse;
import com.behavior.sdk.trigger.visitor.entity.Visitor;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorServiceImpl implements VisitorService {

   private final ProjectRepository projectRepository;
   private final VisitorRepository visitorRepository;

   @Override
   public VisitorResponse createVisitor(String projectKey) {

      Project project = projectRepository.findBySdkKey(projectKey)
            .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 프로젝트 키"));

      Visitor visitor = Visitor.builder()
            .projectId(project.getId())
            .visitorKey(UUID.randomUUID().toString())
            .build();

      Visitor savedVisitor = visitorRepository.save(visitor);

      return toDto(savedVisitor);
   }

   private VisitorResponse toDto(Visitor v) {
      return VisitorResponse.builder()
            .id(v.getId())
            .visitorKey(v.getVisitorKey())
            .projectId(v.getProjectId())
            .createdAt(v.getCreatedAt())
            .build();
   }
}
