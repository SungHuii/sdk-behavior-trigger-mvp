package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.condition.entity.Condition;
import com.behavior.sdk.trigger.condition.repository.ConditionRepository;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.behavior.sdk.trigger.log_event.repository.LogEventRepository;
import com.behavior.sdk.trigger.segment.component.SegmentTriggerJob;
import com.behavior.sdk.trigger.segment.entity.Segment;
import com.behavior.sdk.trigger.segment.repository.SegmentRepository;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SegmentTriggerJobTests {

    @Mock
    ConditionRepository conditionRepository;
    @Mock
    LogEventRepository logEventRepository;
    @Mock
    VisitorRepository visitorRepository;
    @Mock
    SegmentRepository segmentRepository;

    @InjectMocks
    SegmentTriggerJob segmentTriggerJob;

    private Condition cond(UUID projectId, String pageUrl, Integer minEmails) {
        // threshold/operator는 현재 로직에서 사용하지 않으므로 생략
        return Condition.builder()
                .id(UUID.randomUUID())
                .projectId(projectId)
                .eventType(EventType.PAGE_VIEW)
                .pageUrl(pageUrl)
                .segmentMinEmails(minEmails) // null이면 기본 5
                .build();
    }

    @Test
    void run_notCreate_whenUniqueEmailsBelowMin() {
        var pid = UUID.randomUUID();
        var c = cond(pid, "/test", 5);

        when(conditionRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(c));
        // 방문자 로그는 많이 있어도,
        when(logEventRepository.findDistinctVisitorIdsByCondition(c.getId(), c.getPageUrl()))
                .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        // 이메일이 달린 방문자는 2명뿐 → min(5) 미달
        when(visitorRepository.findDistinctEmailsByVisitorIds(anyList()))
                .thenReturn(List.of("a@b.com", "c@d.com"));

        segmentTriggerJob.run();

        verify(segmentRepository, never()).save(any());
    }

    @Test
    void run_notCreate_whenDuplicateSegmentExists() {
        var pid = UUID.randomUUID();
        var c = cond(pid, "/dup", 3);

        when(conditionRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(c));
        when(logEventRepository.findDistinctVisitorIdsByCondition(c.getId(), c.getPageUrl()))
                .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        when(visitorRepository.findDistinctEmailsByVisitorIds(anyList()))
                .thenReturn(List.of("a@b.com", "c@d.com", "e@f.com"));
        when(segmentRepository.existsByConditionIdAndDeletedAtIsNull(c.getId()))
                .thenReturn(true); // 이미 생성됨

        segmentTriggerJob.run();

        verify(segmentRepository, never()).save(any());
    }

    @Test
    void run_create_forEachSatisfiedCondition() {
        var pid = UUID.randomUUID();
        var c1 = cond(pid, "/a", 2);
        var c2 = cond(pid, "/b", 2);

        when(conditionRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(c1, c2));

        when(logEventRepository.findDistinctVisitorIdsByCondition(c1.getId(), c1.getPageUrl()))
                .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID()));
        when(visitorRepository.findDistinctEmailsByVisitorIds(anyList()))
                .thenReturn(List.of("a@b.com", "c@d.com")) // c1 호출용
                .thenReturn(List.of("x@y.com", "z@w.com")); // c2 호출용 (연속 스텁)

        when(segmentRepository.existsByConditionIdAndDeletedAtIsNull(any())).thenReturn(false);

        segmentTriggerJob.run();

        verify(segmentRepository, times(2)).save(any(Segment.class));
    }

    @Test
    void run_excludeVisitorsWithoutEmail_effectivelyByUniqueEmailCount() {
        var pid = UUID.randomUUID();
        var c = cond(pid, "/no-email", 4);

        when(conditionRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(c));
        when(logEventRepository.findDistinctVisitorIdsByCondition(c.getId(), c.getPageUrl()))
                .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        // 실제 이메일이 있는 방문자는 절반뿐 → uniqueEmails.size()=3 < 4
        when(visitorRepository.findDistinctEmailsByVisitorIds(anyList()))
                .thenReturn(List.of("1@a.com", "2@a.com", "3@a.com"));

        when(segmentRepository.existsByConditionIdAndDeletedAtIsNull(c.getId())).thenReturn(false);

        segmentTriggerJob.run();

        verify(segmentRepository, never()).save(any());
    }

    @Test
    void run_create_segment_payload_contains_givenVisitorIds() {
        var pid = UUID.randomUUID();
        var c = cond(pid, "/payload", 2);
        var v1 = UUID.randomUUID();
        var v2 = UUID.randomUUID();

        when(conditionRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(c));
        when(logEventRepository.findDistinctVisitorIdsByCondition(c.getId(), c.getPageUrl()))
                .thenReturn(List.of(v1, v2));
        when(visitorRepository.findDistinctEmailsByVisitorIds(anyList()))
                .thenReturn(List.of("a@b.com", "c@d.com"));
        when(segmentRepository.existsByConditionIdAndDeletedAtIsNull(c.getId())).thenReturn(false);

        ArgumentCaptor<Segment> cap = ArgumentCaptor.forClass(Segment.class);
        segmentTriggerJob.run();

        verify(segmentRepository).save(cap.capture());
        var saved = cap.getValue();
        assertThat(saved.getProjectId()).isEqualTo(pid);
        assertThat(saved.getConditionId()).isEqualTo(c.getId());
        // addVisitorsByIds(visitorIds) 가 정상 반영됐는지 (세부 구현에 맞게 검증)
        assertThat(saved.getSegmentVisitors()).hasSize(2);
    }
}
