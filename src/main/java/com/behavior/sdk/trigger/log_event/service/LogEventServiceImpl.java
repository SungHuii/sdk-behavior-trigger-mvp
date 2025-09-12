package com.behavior.sdk.trigger.log_event.service;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.condition.entity.Condition;
import com.behavior.sdk.trigger.condition.repository.ConditionRepository;
import com.behavior.sdk.trigger.log_event.dto.LogEventCreateRequest;
import com.behavior.sdk.trigger.log_event.dto.LogEventResponse;
import com.behavior.sdk.trigger.log_event.entity.LogEvent;
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
   private final ConditionRepository conditionRepository;

   @Override
   public LogEventResponse createLogEvent(UUID projectId, UUID visitorId, LogEventCreateRequest request) {

      if (!projectRepository.existsById(projectId)) {
         throw new ServiceException(
                 ErrorSpec.LOG_PROJECT_NOT_FOUND,
                    "프로젝트를 찾을 수 없습니다.",
                 List.of(
                         new FieldErrorDetail("projectId", "not found", projectId)
                 )
         );
      }

      if (!visitorRepository.existsById(visitorId)) {
         throw new ServiceException(
                 ErrorSpec.LOG_VISITOR_NOT_FOUND,
                 "방문자를 찾을 수 없습니다.",
                 List.of(
                         new FieldErrorDetail("visitorId", "not found", visitorId)
                 )
         );
      }

      Condition condition = null;
      if (request.getConditionId() != null) {
         condition = conditionRepository.findById(request.getConditionId())
                 .orElseThrow(() -> new ServiceException(
                         ErrorSpec.COND_CONDITION_NOT_FOUND,
                         "조건을 찾을 수 없습니다.",
                         List.of(new FieldErrorDetail("conditionId", "not found", request.getConditionId()))
                 ));
      }

      LogEvent logEvent = LogEvent.builder()
            .projectId(projectId)
            .visitorId(visitorId)
            .condition(condition)
            .eventType(request.getEventType())
            .occurredAt(request.getOccurredAt() != null ? request.getOccurredAt() : LocalDateTime.now())
            .pageUrl(request.getPageUrl())
            .build();
      LogEvent savedLogEvent = logEventRepository.save(logEvent);
      return toDto(savedLogEvent);
   }

   @Override
   public List<LogEventResponse> findLogEvents(UUID projectId, UUID visitorId) {

      if (projectId == null) {
         throw new ServiceException(
                 ErrorSpec.VALID_PARAM_VALIDATION_FAILED,
                    "프로젝트 ID는 필수입니다.",
                 List.of(new FieldErrorDetail("projectId", "required", null))
         );
      }
      if(!projectRepository.existsById(projectId)) {
         throw new ServiceException(
                 ErrorSpec.SYS_FILE_NOT_FOUND,
                    "프로젝트를 찾을 수 없습니다.",
                 List.of(new FieldErrorDetail("projectId", "not found", projectId))
         );
      }

      List<LogEvent> logEventList;
      if (visitorId == null) {
         logEventList = logEventRepository.findAllByProjectId(projectId);
      } else {
         if(!visitorRepository.existsById(visitorId)) {
            throw new ServiceException(
                    ErrorSpec.LOG_VISITOR_NOT_FOUND,
                    "방문자를 찾을 수 없습니다.",
                    List.of(
                            new FieldErrorDetail("visitorId", "not found", visitorId)
                    )
            );
         }
         logEventList = logEventRepository.findAllByProjectIdAndVisitorId(projectId, visitorId);
      }


      return logEventList.stream()
            .map(this::toDto)
            .toList();
   }

   @Override
   public void softDeleteLogEvent(UUID logEventId) {
      LogEvent logEvent = logEventRepository.findById(logEventId)
            .orElseThrow(() -> new ServiceException(
                    ErrorSpec.SYS_FILE_NOT_FOUND,
                    "로그를 찾을 수 없습니다.",
                    List.of(new FieldErrorDetail("logEventId", "not found", logEventId))
            ));

      logEvent.softDelete();
      logEventRepository.save(logEvent);
   }

   private LogEventResponse toDto(LogEvent logEvent) {
      return LogEventResponse.builder()
            .id(logEvent.getId())
            .projectId(logEvent.getProjectId())
            .visitorId(logEvent.getVisitorId())
            .conditionId(logEvent.getCondition() != null ? logEvent.getCondition().getId() : null)
            .eventType(logEvent.getEventType())
            .occurredAt(logEvent.getOccurredAt())
            .createdAt(logEvent.getCreatedAt())
            .pageUrl(logEvent.getPageUrl())
            .build();
   }
}
