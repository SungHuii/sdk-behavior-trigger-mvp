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
import com.behavior.sdk.trigger.user.entity.User;
import com.behavior.sdk.trigger.user.repository.UserRepository;
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
@Tag("integration") // CI 분리용도
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
    @Autowired private UserRepository userRepository;
    private UUID projectId;
    private UUID conditionId;

    @BeforeAll
    void setup() {
        // 혹시 남아있을 이전 데이터 제거 (테스트 간 간섭 방지)
        segmentRepository.deleteAll();
        logEventRepository.deleteAll();
        visitorRepository.deleteAll();
        conditionRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        // 1) 사용자
        User testUser = userRepository.save(User.builder()
                .email("segment-test@example.com")
                .password("encoded-password")
                .build());

        // 2) 프로젝트
        Project project = Project.builder()
                .name("테스트 프로젝트")
                .allowedDomains(List.of("https://example.com"))
                .user(testUser)
                .build();
        projectRepository.save(project);
        projectId = project.getId();

        // 3) 조건 (minEmails = null 이면 기본 5가 적용되도록 실제 로직이 동작)
        Condition condition = Condition.builder()
                .projectId(projectId)
                .eventType(EventType.PAGE_VIEW)
                .operator("greater_than")
                .threshold(1)
                .pageUrl("https://example.com/test")
                // .segmentMinEmails(null)  // 명시하지 않아 기본값 경로를 타게 함
                .build();
        conditionRepository.save(condition);
        conditionId = condition.getId();

        // 4) 방문자/로그 (6명, 동일 URL)
        var fixedNow = LocalDateTime.of(2025, 1, 1, 12, 0);
        for (int i = 0; i < 6; i++) {
            Visitor v = visitorRepository.save(Visitor.builder()
                    .projectId(projectId)
                    .email("test" + i + "@example.com")
                    .build());

            logEventRepository.save(LogEvent.builder()
                    .projectId(projectId)
                    .visitorId(v.getId())
                    .eventType(EventType.PAGE_VIEW)
                    .pageUrl("https://example.com/test")
                    .occurredAt(fixedNow)
                    .condition(condition)
                    .build());
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
        var segments = segmentRepository.findAll();
        assertThat(segments).hasSize(1);
        var created = segments.get(0);
        assertThat(created.getProjectId()).isEqualTo(projectId);
        assertThat(created.getConditionId()).isEqualTo(conditionId);
    }
}
