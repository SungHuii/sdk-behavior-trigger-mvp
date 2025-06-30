package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.condition.entity.Condition;
import com.behavior.sdk.trigger.condition.repository.ConditionRepository;
import com.behavior.sdk.trigger.config.TestSecurityConfig;
import com.behavior.sdk.trigger.log_event.entity.LogEvent;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.behavior.sdk.trigger.log_event.repository.LogEventRepository;
import com.behavior.sdk.trigger.project.entity.Project;
import com.behavior.sdk.trigger.project.repository.ProjectRepository;
import com.behavior.sdk.trigger.segment.component.SegmentTriggerJob;
import com.behavior.sdk.trigger.segment.entity.Segment;
import com.behavior.sdk.trigger.segment.repository.SegmentRepository;
import com.behavior.sdk.trigger.visitor.entity.Visitor;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(TestSecurityConfig.class)
public class AutoCreateSegmentIIntegrationTests {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private ConditionRepository conditionRepository;
    @Autowired private VisitorRepository visitorRepository;
    @Autowired private LogEventRepository logEventRepository;
    @Autowired private SegmentRepository segmentRepository;
    @Autowired private SegmentTriggerJob segmentTriggerJob;

    private UUID projectId;
    private UUID conditionId;

    @BeforeAll
    void setup() {

        // project 생성
        Project project = Project.builder()
                .name("테스트 프로젝트")
                .domain("https://example.com")
                .build();
        projectRepository.save(project);
        projectId = project.getId();

        // condition 생성
        Condition condition = Condition.builder()
                .projectId(projectId)
                .eventType(EventType.PAGE_VIEW)
                .operator("greater_than")
                .threshold(1)
                .pageUrl("https://example.com/test")
                .build();
        conditionRepository.save(condition);
        conditionId = condition.getId();

        // visitor 6명 생성, 로그 생성
        for (int i = 0; i < 6; i++ ) {
            Visitor visitor = Visitor.builder()
                    .projectId(projectId)
                    .email("test" + i + "@example.com")
                    .build();
            visitorRepository.save(visitor);

            LogEvent logEvent = LogEvent.builder()
                    .projectId(projectId)
                    .visitorId(visitor.getId())
                    .eventType(EventType.PAGE_VIEW)
                    .pageUrl("https://example.com/test")
                    .occurredAt(LocalDateTime.now())
                    .condition(condition)
                    .build();
            logEventRepository.save(logEvent);
        }
    }

    // 자동 세그먼트 생성 테스트
    @Test
    @Order(1)
    @Transactional
    void t1_autoCreateSegment() {
        // when
        segmentTriggerJob.run();

        // then
        List<Segment> segments = segmentRepository.findAll();
        assertThat(segments).hasSize(1);

        Segment createdSegment = segments.get(0);
        assertThat(createdSegment.getProjectId()).isEqualTo(projectId);
        assertThat(createdSegment.getConditionId()).isEqualTo(conditionId);
        assertThat(createdSegment.getSegmentVisitors()).hasSize(6);
    }

    // Condition 미충족 시 Segment 생성 불가 테스트
    @Test
    @Order(2)
    @Transactional
    void t2_doNotCreateSegmentWhenConditionNotMet() {

        // given
        Condition condition = Condition.builder()
                .projectId(projectId)
                .eventType(EventType.PAGE_VIEW)
                .operator("greater_than")
                .threshold(10) // threshold를 10으로 설정하여 조건 미충족
                .pageUrl("https://example.com/test")
                .build();

        conditionRepository.save(condition);

        // when
        segmentTriggerJob.run();

        // then
        List<Segment> segments = segmentRepository.findAll();
        assertThat(segments).noneMatch(s -> s.getConditionId().equals(condition.getId()));
    }

    // 중복 세그먼트 생성 방지 테스트
    @Test
    @Order(3)
    @Transactional
    void t3_doNotCreateDuplicateSegments() {

        // when
        segmentTriggerJob.run();

        // then
        List<Segment> segments = segmentRepository.findAll();
        assertThat(segments).hasSize(1); // 이전에 생성된 세그먼트는 유지되어야 함
    }

    // 조건이 여러 개일 때 각각의 조건에 맞는 세그먼트 생성 테스트
    @Test
    @Order(4)
    @Transactional
    void t4_createMultipleSegmentsForMultipleConditions() {

        // given
        Condition anotherCondition = Condition.builder()
                .projectId(projectId)
                .eventType(EventType.PAGE_VIEW)
                .operator("greater_than")
                .threshold(1)
                .pageUrl("https://example.com/another-test")
                .build();
        conditionRepository.save(anotherCondition);

        // 로그 6개 생성 (다른 URL)
        for (int i = 0; i < 6; i++) {
            Visitor visitor =Visitor.builder()
                    .projectId(projectId)
                    .email("another" + i + "@example.com")
                    .build();
            visitorRepository.save(visitor);

            logEventRepository.save(LogEvent.builder()
                    .projectId(projectId)
                    .visitorId(visitor.getId())
                    .eventType(EventType.PAGE_VIEW)
                    .pageUrl("https://example.com/another-test")
                    .occurredAt(LocalDateTime.now())
                    .condition(anotherCondition)
                    .build());
        }

        // when
        segmentTriggerJob.run();

        // then
        List<Segment> segments = segmentRepository.findAll();
        assertThat(segments).hasSize(2); // 두 개의 조건에 대해 각각 세그먼트가 생성되어야 함
    }

    // 이메일이 없는 Visitor 제외 테스트
    @Test
    @Order(5)
    @Transactional
    void t5_excludeVisitorsWithoutEmail() {

        // given
        Condition condition = Condition.builder()
                .projectId(projectId)
                .eventType(EventType.PAGE_VIEW)
                .operator("greater_than")
                .threshold(1)
                .pageUrl("https://example.com/no-email")
                .build();
        conditionRepository.save(condition);

        for (int i = 0; i < 6; i++) {
            Visitor visitor = Visitor.builder()
                    .projectId(projectId)
                    .email(i % 2 == 0 ? "email" + i + "@example.com" : null) // 홀수 인덱스는 이메일 없음
                    .build();
            visitorRepository.save(visitor);

            logEventRepository.save(LogEvent.builder()
                    .projectId(projectId)
                    .visitorId(visitor.getId())
                    .eventType(EventType.PAGE_VIEW)
                    .pageUrl("https://example.com/no-email")
                    .occurredAt(LocalDateTime.now())
                    .condition(condition)
                    .build());
        }

        // when
        segmentTriggerJob.run();

        // then
        List<Segment> segments = segmentRepository.findAll();
        Segment segment = segments.stream()
                .filter(s -> s.getConditionId().equals(condition.getId()))
                .findFirst()
                .orElse(null);

        assertThat(segment).isNull();
    }
}
