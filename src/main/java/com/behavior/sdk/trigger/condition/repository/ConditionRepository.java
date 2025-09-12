package com.behavior.sdk.trigger.condition.repository;

import com.behavior.sdk.trigger.condition.entity.Condition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConditionRepository extends JpaRepository<Condition, UUID> {
    List<Condition> findAllByProjectIdAndDeletedAtIsNull(UUID projectId);

    List<Condition> findByProjectId(UUID projectId);

    List<Condition> findAllByDeletedAtIsNull();
    boolean existsByIdAndDeletedAtIsNull(UUID conditionId);
}
