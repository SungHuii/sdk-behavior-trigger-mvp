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

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorServiceImpl implements VisitorService {

   private final ProjectRepository projectRepository;
   private final VisitorRepository visitorRepository;

   @Override
   public VisitorResponse createVisitor(UUID projectId) {

      projectRepository.findById(projectId)
            .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 프로젝트 키"));

      Visitor visitor = Visitor.builder()
              .projectId(projectId)
              .build();

      Visitor savedVisitor = visitorRepository.save(visitor);

      return toDto(savedVisitor);
   }

   @Override
   public boolean existsVisitor(UUID visitorId) {
      return visitorRepository.existsByIdAndDeletedAtIsNull(visitorId);
   }

   private VisitorResponse toDto(Visitor v) {
      return VisitorResponse.builder()
            .id(v.getId())
            .projectId(v.getProjectId())
            .createdAt(v.getCreatedAt())
            .build();
   }

   @Override
   public void updateEmail(UUID visitorId, String email) {
      Visitor visitor = visitorRepository.findById(visitorId)
              .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 방문자 키 :" + visitorId));

      visitor.setEmail(email);
      visitorRepository.save(visitor);
   }
}
