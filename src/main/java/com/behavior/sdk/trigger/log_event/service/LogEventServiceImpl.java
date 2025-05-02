package com.behavior.sdk.trigger.log_event.service;

import com.behavior.sdk.trigger.log_event.dto.LogEventCreateRequest;
import com.behavior.sdk.trigger.log_event.dto.LogEventResponse;
import com.behavior.sdk.trigger.log_event.entity.LogEvent;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.behavior.sdk.trigger.log_event.repository.LogEventRepository;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LogEventServiceImpl implements LogEventService {

   private final LogEventRepository logEventRepository;
   private final ProjectRepository projectRepository;
   private final VisitorRepository visitorRepository;

   @Override
   public LogEventResponse createLogEvent(UUID projectId, UUID visitorId, LogEventCreateRequest request) {

      if (!projectRepository.existsById(projectId)) {
         throw new EntityNotFoundException("프로젝트를 찾을 수 없습니다.");
      }

      if (!visitorRepository.existsById(visitorId)) {
         throw new EntityNotFoundException("방문자를 찾을 수 없습니다.");
      }

      LogEvent logEvent = LogEvent.builder()
            .projectId(projectId)
            .visitorId(visitorId)
            .eventType(request.getEventType())
            .occurredAt(request.getOccurredAt() != null ? request.getOccurredAt() : LocalDateTime.now())
            .pageUrl(request.getPageUrl())
            .build();
      LogEvent savedLogEvent = logEventRepository.save(logEvent);
      return toDto(savedLogEvent);
   }

   @Override
   public List<LogEventResponse> findLogEvents(UUID projectId, UUID visitorId) {

      return logEventRepository.findAllByProjectIdAndVisitorId(projectId,visitorId)
            .stream().map(this::toDto).toList();
   }

   @Override
   public void softDeleteLogEvent(UUID logEventId) {
      LogEvent logEvent = logEventRepository.findById(logEventId)
            .orElseThrow(() -> new EntityNotFoundException("로그를 찾을 수 없습니다."));

      logEvent.softDelete();
      logEventRepository.save(logEvent);
   }

   private LogEventResponse toDto(LogEvent logEvent) {
      return LogEventResponse.builder()
            .id(logEvent.getId())
            .projectId(logEvent.getProjectId())
            .visitorId(logEvent.getVisitorId())
            .eventType(logEvent.getEventType())
            .occurredAt(logEvent.getOccurredAt())
            .createdAt(logEvent.getCreatedAt())
            .pageUrl(logEvent.getPageUrl())
            .build();
   }
}
