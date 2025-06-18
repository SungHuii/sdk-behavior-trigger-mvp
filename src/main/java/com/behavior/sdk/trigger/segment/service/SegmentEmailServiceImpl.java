package com.behavior.sdk.trigger.segment.service;

import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email.service.EmailService;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepository;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepositoryImpl;
import com.behavior.sdk.trigger.segment.dto.EmailBatchResponse;
import com.behavior.sdk.trigger.segment.entity.EmailBatch;
import com.behavior.sdk.trigger.segment.repository.EmailBatchRepository;
import com.behavior.sdk.trigger.segment.repository.SegmentVisitorRepository;
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
public class SegmentEmailServiceImpl implements SegmentEmailService{

    private final SegmentVisitorRepository segmentVisitorRepository;
    private final EmailService emailService;
    private final EmailBatchRepository emailBatchRepository;
    private final EmailLogRepository emailLogRepository;

    @Override
    public EmailBatchResponse sendEmailBatch(UUID segmentId) {
        log.info("=== sendEmailBatch 시작: segmentId={}", segmentId);
        List<UUID> visitorIds = segmentVisitorRepository.findVisitorIdsBySegmentId(segmentId);

        List<UUID> notSentVisitorIds = visitorIds.stream()
                .filter(visitorId -> !emailLogRepository.existsByVisitorId(visitorId))
                .toList();

        EmailBatch emailBatch = EmailBatch.builder()
                .segmentId(segmentId)
                .sentCount(0)
                .failedCount(0)
                .build();
        emailBatch = emailBatchRepository.saveAndFlush(emailBatch);  // ID/Version 초기화
        log.info("=== EmailBatch saveAndFlush 성공: batchId={}", emailBatch.getId());

        UUID batchId = emailBatch.getId();


        for (UUID visitorId : notSentVisitorIds) {
            try {
                emailService.sendEmail(
                        EmailSendRequest.builder()
                                .visitorId(visitorId)
                                .build()
                );
                log.info("=== emailService.sendEmail 성공: visitorId={}", visitorId);

                emailLogRepository.save(EmailLog.builder()
                        .visitorId(visitorId)
                        .batchId(batchId)
                        .status(EmailStatus.SENT)
                        .build());
                log.info("=== EmailLog save 성공: visitorId={}", visitorId);

            } catch (Exception e) {
                log.error("=== EmailLog save 실패 (FAILED 기록): visitorId={}, error={}", visitorId, e.getMessage(), e);
                emailLogRepository.save(EmailLog.builder()
                        .visitorId(visitorId)
                        .batchId(batchId)
                        .status(EmailStatus.FAILED)
                        .errorMessage(e.getMessage())
                        .build());
            }

        }
        log.info("=== sendEmailBatch 끝: batchId={}", emailBatch.getId());

        long sent = emailLogRepository.countByBatchIdAndStatus(batchId, EmailStatus.SENT);
        long failed = emailLogRepository.countByBatchIdAndStatus(batchId, EmailStatus.FAILED);

        return EmailBatchResponse.builder()
                .batchId(batchId)
                .sentCount((int) sent)
                .failedCount((int) failed)
                .build();
    }

}
