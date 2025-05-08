package com.behavior.sdk.trigger.email_template.service;

import com.behavior.sdk.trigger.email_template.entity.EmailTemplate;

import java.util.List;
import java.util.UUID;

public interface EmailTemplateService {

    EmailTemplate createTemplate(EmailTemplate emailTemplate);
    List<EmailTemplate> listByCondition(UUID conditionId);
    void softDeleteTemplate(UUID templateId);
}
