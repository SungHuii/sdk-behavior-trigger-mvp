package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.condition.entity.Condition;
import com.behavior.sdk.trigger.condition.repository.ConditionRepository;
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
}
