package com.behavior.sdk.trigger.condition.service;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.condition.dto.ConditionCreateRequest;
import com.behavior.sdk.trigger.condition.dto.ConditionResponse;
import com.behavior.sdk.trigger.condition.entity.Condition;
import com.behavior.sdk.trigger.condition.repository.ConditionRepository;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConditionServiceImpl implements ConditionService {

    private final ConditionRepository conditionRepository;

    @Override
    public ConditionResponse createCondition(ConditionCreateRequest request) {
        Condition condition = Condition.builder()
                .projectId(request.getProjectId())
                .eventType(request.getEventType())
                .operator(request.getOperator())
                .threshold(request.getThreshold())
                .pageUrl(request.getPageUrl())
                .segmentMinEmails(request.getSegmentMinEmails())
                .build();
        Condition savedCondition = conditionRepository.save(condition);
        return toDto(savedCondition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConditionResponse> listConditions(UUID projectId) {
        return conditionRepository.findAllByProjectIdAndDeletedAtIsNull(projectId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void softDeleteCondition(UUID conditionId) {
        Condition condition = conditionRepository.findById(conditionId)
                .orElseThrow(() -> new ServiceException(
                        ErrorSpec.COND_CONDITION_NOT_FOUND,
                        "존재하지 않는 조건입니다.",
                        List.of(new FieldErrorDetail("conditionId", "not found", conditionId))
                ));
        condition.softDelete();
        conditionRepository.save(condition);
    }

    private ConditionResponse toDto(Condition condition) {
        return ConditionResponse.builder()
                .id(condition.getId())
                .projectId(condition.getProjectId())
                .eventType(condition.getEventType())
                .operator(condition.getOperator())
                .threshold(condition.getThreshold())
                .pageUrl(condition.getPageUrl())
                .segmentMinEmails(condition.getSegmentMinEmails())
                .createdAt(condition.getCreatedAt())
                .deletedAt(condition.getDeletedAt())
                .build();
    }
}
