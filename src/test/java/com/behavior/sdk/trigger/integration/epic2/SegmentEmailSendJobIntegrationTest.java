package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import com.behavior.sdk.trigger.email_log.repository.EmailLogRepository;
import com.behavior.sdk.trigger.segment.component.SegmentEmailSendJob;
import com.behavior.sdk.trigger.segment.entity.EmailBatch;
import com.behavior.sdk.trigger.segment.entity.Segment;
import com.behavior.sdk.trigger.segment.entity.SegmentVisitor;
import com.behavior.sdk.trigger.segment.repository.EmailBatchRepository;
import com.behavior.sdk.trigger.segment.repository.SegmentRepository;
import com.behavior.sdk.trigger.segment.repository.SegmentVisitorRepository;
import com.behavior.sdk.trigger.visitor.entity.Visitor;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SegmentEmailSendJobIntegrationTest {

    @Autowired SegmentRepository segmentRepository;
    @Autowired SegmentVisitorRepository segmentVisitorRepository;
    @Autowired VisitorRepository visitorRepository;
    @Autowired EmailBatchRepository emailBatchRepository;
    @Autowired EmailLogRepository emailLogRepository;
    @Autowired SegmentEmailSendJob segmentEmailSendJob;

    @BeforeEach
    void cleanAndSetup() {
        emailLogRepository.deleteAllInBatch();
        emailBatchRepository.deleteAllInBatch();
        segmentVisitorRepository.deleteAllInBatch();
        visitorRepository.deleteAllInBatch();
        segmentRepository.deleteAllInBatch();

        UUID projectId = UUID.randomUUID();

        Segment segment = segmentRepository.saveAndFlush(
                Segment.builder()
                        .conditionId(UUID.randomUUID())
                        .projectId(projectId)
                        .processedAt(null)
                        .build()
        );

        for (int i = 0; i < 3; i++) {
            Visitor visitor = visitorRepository.saveAndFlush(
                    Visitor.builder()
                            .email("test" + i + "@example.com")
                            .projectId(projectId)
                            .build()
            );
            segmentVisitorRepository.saveAndFlush(new SegmentVisitor(segment, visitor.getId()));
        }
    }

    @Test
    void segmentEmailSendJob_shouldSendEmailsAndLogCorrectly() {
        // WHEN
        segmentEmailSendJob.run();

        // THEN
        assertThat(segmentRepository.findAll()).hasSize(1);
        Segment segment = segmentRepository.findAll().get(0);
        assertThat(segment.getProcessedAt()).isNotNull();

        List<EmailBatch> batches = emailBatchRepository.findAll();
        assertThat(batches).hasSize(1);

        List<EmailLog> logs = emailLogRepository.findAll();
        assertThat(logs).hasSize(3);
    }
}
