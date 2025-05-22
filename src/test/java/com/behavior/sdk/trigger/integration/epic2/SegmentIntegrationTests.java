package com.behavior.sdk.trigger.integration.epic2;

import com.behavior.sdk.trigger.email_template.entity.EmailTemplate;
import com.behavior.sdk.trigger.email_template.repository.EmailTemplateRepository;
import com.behavior.sdk.trigger.segment.dto.SegmentCreateRequest;
import com.behavior.sdk.trigger.segment.entity.Segment;
import com.behavior.sdk.trigger.segment.repository.SegmentRepository;
import com.behavior.sdk.trigger.visitor.entity.Visitor;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SegmentIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private SegmentRepository segmentRepository;

    @Autowired
    private VisitorRepository visitorRepository;
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    private UUID projectId;
    private UUID conditionId;
    private UUID segmentId;

    private UUID visitorId;
    private UUID templateId;

    @BeforeAll
    void setup() throws Exception {

        String projectJson = mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Segment Test Project\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        projectId = UUID.fromString(om.readTree(projectJson).get("id").asText());

        Map<String, Object> condition = Map.of(
                "projectId", projectId.toString(),
                "eventType", "page_view",
                "operator", "GREATER_THAN",
                "threshold", 0,
                "pageUrl", "https://example.com"
        );

        String conditionJson = mockMvc.perform(post("/api/conditions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(condition)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        conditionId = UUID.fromString(om.readTree(conditionJson).get("id").asText());

        String visitorJson = mockMvc.perform(post("/api/visitors")
                .param("projectId", projectId.toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID visitorId = UUID.fromString(om.readTree(visitorJson).get("id").asText());

        mockMvc.perform(post("/api/logs")
                .param("projectId", projectId.toString())
                .param("visitorId", visitorId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
              "eventType": "page_view",
              "occurredAt": "2025-05-20T10:00:00",
              "conditionId": "%s",
              "pageUrl": "https://example.com"
            }
            """.formatted(conditionId)))
        .andExpect(status().isCreated());

        visitorId = UUID.fromString(om.readTree(visitorJson).get("id").asText());
        Visitor visitor = visitorRepository.findById(visitorId).orElseThrow();
        visitor.setEmail("test@example.com");
        visitorRepository.save(visitor);

        EmailTemplate template = EmailTemplate.builder()
                .conditionId(conditionId)
                .subject("테스트 이메일입니다.")
                .body("테스트 이메일 본문입니다.")
                .build();
        template = emailTemplateRepository.save(template);
        templateId = template.getId();
    }

    @Test
    @Order(1)
    @DisplayName("Segment 생성")
    void t1_createSegment() throws Exception {
        SegmentCreateRequest segmentRequest = SegmentCreateRequest.builder()
                .projectId(projectId)
                .conditionId(conditionId)
                .build();

        String segmentResponse = mockMvc.perform(post("/api/segments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(segmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.visitorIds", hasSize(1)))
                .andReturn().getResponse().getContentAsString();

        segmentId = UUID.fromString(om.readTree(segmentResponse).get("id").asText());

        Segment segment = segmentRepository.findById(segmentId).orElseThrow();
        assertThat(segment.getConditionId()).isEqualTo(conditionId);
    }

    @Test
    @Order(2)
    @DisplayName("Segment 목록 조회")
    void t2_listSegments() throws Exception {
        mockMvc.perform(get("/api/segments")
                .param("projectId", projectId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @Order(3)
    @DisplayName("세그먼트 단건 조회")
    void t3_getSegment() throws Exception {
        mockMvc.perform(get("/api/segments/{id}", segmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(segmentId.toString()))
                .andExpect(jsonPath("$.visitorIds", hasSize(1)));
    }

    @Test
    @Order(4)
    @DisplayName("세그먼트 삭제(soft)")
    void t4_deleteSegment() throws Exception {
        mockMvc.perform(delete("/api/segments/{id}", segmentId))
                .andExpect(status().isNoContent());

        // 삭제 후에도 GET 하면 404 또는 visitorIds 빈 배열이 나오도록 설계에 따라 검증
        mockMvc.perform(get("/api/segments/{id}", segmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deletedAt").exists());
    }

    @Test
    @Order(5)
    @DisplayName("세그먼트 대상 일괄 이메일 전송")
    void t5_sendSegmentEmailBatch() throws Exception {
        String payload = """
        {
          "templateId": "%s"
        }
        """.formatted(templateId);

        mockMvc.perform(post("/api/segments/{id}/send-email", segmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").exists())
                .andExpect(jsonPath("$.sentCount").value(1))
                .andExpect(jsonPath("$.failedCount").value(0));
    }
}
