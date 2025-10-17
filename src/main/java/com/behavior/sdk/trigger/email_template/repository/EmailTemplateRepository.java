package com.behavior.sdk.trigger.email_template.repository;

import com.behavior.sdk.trigger.email_template.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID> {
    List<EmailTemplate> findAllByConditionIdAndDeletedAtIsNull(UUID conditionId);

    @Query("SELECT et FROM EmailTemplate et WHERE et.conditionId = :conditionId AND et.deletedAt IS NULL ORDER BY et.updatedAt DESC")
    Optional<EmailTemplate> findLatestByConditionId(@Param("conditionId") UUID conditionId);
}
