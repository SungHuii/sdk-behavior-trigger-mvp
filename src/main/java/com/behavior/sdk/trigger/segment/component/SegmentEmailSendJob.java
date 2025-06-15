package com.behavior.sdk.trigger.segment.component;

import com.behavior.sdk.trigger.email.service.EmailService;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepository;
import com.behavior.sdk.trigger.email_template.repository.EmailTemplateRepository;
import com.behavior.sdk.trigger.segment.entity.Segment;
import com.behavior.sdk.trigger.segment.repository.EmailBatchRepository;
import com.behavior.sdk.trigger.segment.repository.SegmentRepository;
import com.behavior.sdk.trigger.segment.repository.SegmentVisitorRepository;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SegmentEmailSendJob {

    private final SegmentRepository segmentRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final SegmentVisitorRepository segmentVisitorRepository;
    private final VisitorRepository visitorRepository;
    private final EmailLogRepository emailLogRepository;
    private final EmailBatchRepository emailBatchRepository;
    private final EmailService emailService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void run() {
        log.info("[SegmentEmailSendJob] 시작 - 세그먼트 이메일 발송 작업");

        List<Segment> segments = segmentRepository.findAll();
    }

}
