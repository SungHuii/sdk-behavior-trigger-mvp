package com.behavior.sdk.trigger.log.service;

import com.behavior.sdk.trigger.log.dto.LogCreateRequest;
import com.behavior.sdk.trigger.log.dto.LogResponse;
import com.behavior.sdk.trigger.log.entity.Log;
import com.behavior.sdk.trigger.log.enums.EventType;
import com.behavior.sdk.trigger.log.repository.LogRepository;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LogServiceImpl implements LogService {

   private final ProjectRepository projectRepository;
   private final VisitorRepository visitorRepository;
   private final LogRepository logRepository;

   @Override
   public LogResponse createLog(String projectKey, String visitorKey,LogCreateRequest request) {

      UUID projectId = projectRepository.findBySdkKey(projectKey)
            .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 프로젝트 키"))
            .getId();

      UUID visitorId = visitorRepository.findByVisitorKey(visitorKey)
            .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 방문자 키"))
            .getId();

      long duration = request.getDurationMs() != null
            ? request.getDurationMs()
            : 0L;
      LocalDateTime occurredAt = request.getOccurredAt() != null
            ? request.getOccurredAt()
            : LocalDateTime.now();

      Log log = Log.builder()
            .projectId(projectId)
            .visitorId(visitorId)
            .eventType(request.getEventType().name())
            .durationMs(duration)
            .occurredAt(occurredAt)
            .build();
      Log savedLog = logRepository.save(log);

      return LogResponse.builder().
            id(savedLog.getId())
            .projectId(savedLog.getProjectId())
            .eventType(EventType.valueOf(savedLog.getEventType()))
            .durationMs(savedLog.getDurationMs())
            .occurredAt(savedLog.getOccurredAt())
            .createdAt(savedLog.getCreatedAt())
            .build();
   }
}
