package com.behavior.sdk.trigger.email_template.repository;

import com.behavior.sdk.trigger.email_template.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID> {
    List<EmailTemplate> findAllByConditionIdAndDeletedAtIsNull(UUID conditionId);
}
