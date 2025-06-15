package com.behavior.sdk.trigger.segment.service;

import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email.service.EmailService;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepository;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepositoryImpl;
import com.behavior.sdk.trigger.segment.dto.EmailBatchResponse;
import com.behavior.sdk.trigger.segment.entity.EmailBatch;
import com.behavior.sdk.trigger.segment.repository.EmailBatchRepository;
import com.behavior.sdk.trigger.segment.repository.SegmentVisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SegmentEmailServiceImpl implements SegmentEmailService{

    private final SegmentVisitorRepository segmentVisitorRepository;
    private final EmailService emailService;
    private final EmailBatchRepository emailBatchRepository;
    private final EmailLogRepository emailLogRepository;
    private final EmailLogRepositoryImpl emailLogRepositoryImpl;

    @Override
    public EmailBatchResponse sendEmailBatch(UUID segmentId) {

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
        UUID batchId = emailBatch.getId();


        for (UUID visitorId : notSentVisitorIds) {
            try {
                emailService.sendEmail(
                        EmailSendRequest.builder()
                                .visitorId(visitorId)
                                .build()
                );
                emailLogRepositoryImpl.saveSentLog(visitorId, batchId);
            } catch (Exception e) {
                emailLogRepositoryImpl.saveFailedLog(visitorId, batchId, e.getMessage());
            }
        }

        long sent = emailLogRepositoryImpl.countByBatchIdAndStatus(batchId, EmailStatus.SENT);
        long failed = emailLogRepositoryImpl.countByBatchIdAndStatus(batchId, EmailStatus.FAILED);

        return EmailBatchResponse.builder()
                .batchId(batchId)
                .sentCount((int) sent)
                .failedCount((int) failed)
                .build();
    }

}
