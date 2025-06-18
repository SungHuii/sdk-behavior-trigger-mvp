package com.behavior.sdk.trigger.email_log.service;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailLogServiceImpl implements EmailLogService{

    private final EmailLogRepository emailLogRepository;

    @Override
    public EmailLog createEmailLog(UUID visitorId, UUID templateId, EmailStatus status) {
        log.info("Creating EmailLog: visitorId={}, templateId={}, status={}", visitorId, templateId, status);

        EmailLog emailLog = EmailLog.builder()
                .visitorId(visitorId)
                .templateId(null)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        log.info("EmailLog 생성 시 데이터: id={}, visitorId={}, templateId={}, status={}",
                emailLog.getId(), emailLog.getVisitorId(), emailLog.getTemplateId(), emailLog.getStatus());
        emailLog = emailLogRepository.save(emailLog);

        return emailLog;
    }

    @Override
    public List<EmailLog> listLogsByVisitorId(UUID visitorId) {
        return emailLogRepository.findAllByVisitorIdAndDeletedAtIsNull(visitorId);
    }

    @Override
    public void softDeleteEmailLog(UUID emailLogId) {
        log.info("Soft delete requested: emailLogId={}", emailLogId);

        EmailLog emailLog = emailLogRepository.findById(emailLogId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 이메일 로그 ID"));
        log.info("Found EmailLog: id={}, status={}, deletedAt={}", emailLog.getId(), emailLog.getStatus(), emailLog.getDeletedAt());

        emailLog.setDeletedAt(LocalDateTime.now());
        log.info("Setting deletedAt: id={}, deletedAt={}", emailLog.getId(), emailLog.getDeletedAt());

        emailLogRepository.save(emailLog);
    }


}
