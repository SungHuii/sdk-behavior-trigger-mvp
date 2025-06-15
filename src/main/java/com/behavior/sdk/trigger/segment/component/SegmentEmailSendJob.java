package com.behavior.sdk.trigger.segment.component;

import com.behavior.sdk.trigger.segment.entity.Segment;
import com.behavior.sdk.trigger.segment.repository.SegmentRepository;
import com.behavior.sdk.trigger.segment.service.SegmentEmailService;
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
    private final SegmentEmailService segmentEmailService;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void run() {
        log.info("[SegmentEmailSendJob] 시작 - 세그먼트 이메일 발송 작업");

        List<Segment> unprocessedSegments = segmentRepository.findAllUnprocessed();
        log.info("처리할 세그먼트 수: {}", unprocessedSegments.size());

        for (Segment segment : unprocessedSegments) {
            try {
                log.info("Segment ID : {} - 이메일 발송 시작", segment.getId());

                segmentEmailService.sendEmailBatch(segment.getId());

                segment.markAsProcessed();
//                segmentRepository.save(segment);

                log.info("Segment ID : {} - 이메일 발송 완료", segment.getId());
            } catch (Exception e) {
                log.error("Segment ID : {} - 이메일 발송 중 오류 발생: {}", segment.getId(), e.getMessage());
            }
        }

        log.info("[SegmentEmailSendJob] 완료 - 세그먼트 이메일 발송 작업");
    }
}
