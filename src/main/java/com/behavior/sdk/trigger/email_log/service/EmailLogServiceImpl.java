package com.behavior.sdk.trigger.email_log.service;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailLogServiceImpl implements EmailLogService{

    private final EmailLogRepository emailLogRepository;

    @Override
    public EmailLog createEmailLog(UUID visitorId, UUID templateId, EmailStatus status) {
        EmailLog emailLog = EmailLog.builder()
                .id(UUID.randomUUID())
                .visitorId(visitorId)
                .templateId(templateId)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        return emailLogRepository.save(emailLog);
    }

    @Override
    public List<EmailLog> listLogsByVisitorId(UUID visitorId) {
        return emailLogRepository.findAllByVisitorIdAndDeletedAtIsNull(visitorId);
    }

    @Override
    public void softDeleteEmailLog(UUID emailLogId) {
        EmailLog emailLog = emailLogRepository.findById(emailLogId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일 로그 ID"));
        emailLog.setDeletedAt(LocalDateTime.now());
        emailLogRepository.save(emailLog);
    }


}
