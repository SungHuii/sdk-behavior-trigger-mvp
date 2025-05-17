package com.behavior.sdk.trigger.condition.service;

import com.behavior.sdk.trigger.condition.dto.ConditionCreateRequest;
import com.behavior.sdk.trigger.condition.dto.ConditionResponse;

import java.util.List;
import java.util.UUID;

public interface ConditionService {

    ConditionResponse createCondition(ConditionCreateRequest request);
    List<ConditionResponse> listConditions(UUID projectId);
    void softDeleteCondition(UUID conditionId);
}
