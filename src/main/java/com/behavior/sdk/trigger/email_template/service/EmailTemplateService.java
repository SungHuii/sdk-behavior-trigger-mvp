package com.behavior.sdk.trigger.email_template.service;

import com.behavior.sdk.trigger.email_template.entity.EmailTemplate;
import com.behavior.sdk.trigger.visitor.entity.Visitor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailTemplateService {

    EmailTemplate createTemplate(EmailTemplate emailTemplate);
    List<EmailTemplate> listByCondition(UUID conditionId);
    Optional<EmailTemplate> findLatestActiveByConditionId(UUID conditionId);
    void softDeleteTemplate(UUID templateId);
    Optional<EmailTemplate> findById(UUID templateId);

}
