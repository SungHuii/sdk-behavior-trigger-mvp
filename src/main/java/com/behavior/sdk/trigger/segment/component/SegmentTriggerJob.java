package com.behavior.sdk.trigger.segment.component;

import com.behavior.sdk.trigger.condition.entity.Condition;
import com.behavior.sdk.trigger.condition.repository.ConditionRepository;
import com.behavior.sdk.trigger.log_event.repository.LogEventRepository;
import com.behavior.sdk.trigger.segment.entity.Segment;
import com.behavior.sdk.trigger.segment.repository.SegmentRepository;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SegmentTriggerJob {

    private final ConditionRepository conditionRepository;
    private final LogEventRepository logEventRepository;
    private final VisitorRepository visitorRepository;
    private final SegmentRepository segmentRepository;

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    @Transactional
    public void run() {
        log.info("[SegmentTriggerJob] 세그먼트 자동 생성 스케줄러 실행 시작");

        List<Condition> conditions = conditionRepository.findAllByDeletedAtIsNull();
        log.info("총 조건 수 : {}", conditions.size());

        for (Condition condition : conditions) {

            UUID conditionId = condition.getId();
            UUID projectId = condition.getProjectId();
            Integer threshold = condition.getThreshold();
            String pageUrl = condition.getPageUrl();

            int minEmails = (condition.getSegmentMinEmails() != null) ? condition.getSegmentMinEmails() : 5;

            List<UUID> visitorIds = logEventRepository.findDistinctVisitorIdsByCondition(conditionId, pageUrl);
            List<String> uniqueEmails = visitorRepository.findDistinctEmailsByVisitorIds(visitorIds);

            log.info("조건 ID : {} -> 유효 visitor 수 : {}, 유니크 이메일 수 : {} (기준: {})",
                    conditionId, visitorIds.size(), uniqueEmails.size(), minEmails);

            boolean notAlreadySegmented = !segmentRepository.existsByConditionIdAndDeletedAtIsNull(conditionId);
            if (uniqueEmails.size() >= minEmails && notAlreadySegmented) {
                Segment segment = new Segment();
                segment.setProjectId(projectId);
                segment.setConditionId(conditionId);
                segment.addVisitorsByIds(visitorIds);
                segmentRepository.save(segment);
                log.info("[Segment Trigger] 세그먼트 생성 완료 → conditionId={}, visitor 수: {}, 이메일 수: {}", conditionId, visitorIds.size(), uniqueEmails.size());
            } else {
                log.info("[Segment Trigger] 조건 불충족 → conditionId={}, 유니크 이메일 수: {} < 기준값(5)", conditionId, uniqueEmails.size());
            }
            log.info("실제 visitorIds: {}", visitorIds);
            log.info("이메일이 있는 visitorIds: {}", uniqueEmails);
            log.info("실제 비교되는 pageUrl: {}", pageUrl);
            log.info("조건ID: {}, pageUrl: {}, threshold: {}", conditionId, pageUrl, threshold);
            log.info("visitorIds 쿼리 결과: {}", visitorIds);

            log.info("[SegmentTriggerJob] 세그먼트 자동 생성 스케줄러 실행 완료");
        }
    }
}
