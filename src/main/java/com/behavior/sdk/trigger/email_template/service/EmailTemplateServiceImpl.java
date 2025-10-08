package com.behavior.sdk.trigger.email_template.service;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.email_template.entity.EmailTemplate;
import com.behavior.sdk.trigger.email_template.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailTemplateServiceImpl implements EmailTemplateService{

    private final EmailTemplateRepository emailTemplateRepository;

    @Override
    public EmailTemplate createTemplate(EmailTemplate emailTemplate) {
        return emailTemplateRepository.save(emailTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplate> listByCondition(UUID conditionId) {
        return emailTemplateRepository.findAllByConditionIdAndDeletedAtIsNull(conditionId);
    }

    @Override
    public void softDeleteTemplate(UUID templateId) {
        EmailTemplate emailTemplate = emailTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ServiceException(
                        ErrorSpec.SYS_FILE_NOT_FOUND,
                        "존재하지 않는 이메일 템플릿입니다.",
                        List.of(new FieldErrorDetail("templateId", "not found", templateId))
                ));
        emailTemplate.setDeletedAt(LocalDateTime.now());
        emailTemplateRepository.save(emailTemplate);
    }
}
