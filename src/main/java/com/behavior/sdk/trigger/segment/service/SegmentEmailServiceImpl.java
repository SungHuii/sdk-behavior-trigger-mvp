package com.behavior.sdk.trigger.segment.service;

import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.service.EmailService;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepository;
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

    @Override
    public EmailBatchResponse sendEmailBatch(UUID segmentId, UUID templateId) {

        List<UUID> visitorIds = segmentVisitorRepository.findVisitorIdsBySegmentId(segmentId);

        UUID batchId = UUID.randomUUID();
        for (UUID visitorId : visitorIds) {
            emailService.sendEmail(
                    EmailSendRequest.builder()
                            .visitorId(visitorId)
                            .templateId(templateId)
                            .build()
            );
        }

        long sent = emailLogRepository.countSentBySegment(segmentId);
        long failed = emailLogRepository.countFailedBySegment(segmentId);

/*
        int sentCount = 0;
        int failedCount = 0;

        for (UUID visitorId : visitorIds) {
            try {
                emailService.sendEmail(
                        EmailSendRequest.builder()
                                .visitorId(visitorId)
                                .templateId(templateId)
                                .build()
                );
                sentCount++;
            } catch (Exception e) {
                failedCount++;
            }
        }
*/

/*        EmailBatch emailBatch = EmailBatch.builder()
                .segmentId(segmentId)
                .sentCount(sentCount)
                .failedCount(failedCount)
                .createdAt(LocalDateTime.now())
                .build();

        emailBatchRepository.save(emailBatch);*/

        return EmailBatchResponse.builder()
                .batchId(batchId)
                .sentCount((int) sent)
                .failedCount((int) failed)
                .build();
    }

}
