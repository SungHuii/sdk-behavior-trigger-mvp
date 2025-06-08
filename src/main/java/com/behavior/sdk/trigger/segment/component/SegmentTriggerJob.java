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
        List<Condition> conditions = conditionRepository.findAll();

        for (Condition condition : conditions) {

            UUID conditionId = condition.getId();
            UUID projectId = condition.getProjectId();
            Integer threshold = condition.getThreshold();
            String pageUrl = condition.getPageUrl();

            List<UUID> visitorIds = logEventRepository.findVisitorIdsByCondition(conditionId, threshold, pageUrl);
            List<String> uniqueEmails = visitorRepository.findDistinctEmailsByVisitorIds(visitorIds);

            if (uniqueEmails.size() >= 5) {
                Segment segment = new Segment();
                segment.setProjectId(projectId);
                segment.setConditionId(conditionId);
                segment.addVisitorsByIds(visitorIds);
                segmentRepository.save(segment);
                log.info("[Segment Trigger] Created segment for conditionId={} with {} unique visitors", conditionId, uniqueEmails.size());
            }
        }
    }
}
