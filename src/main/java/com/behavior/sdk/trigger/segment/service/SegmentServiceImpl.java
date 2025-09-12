package com.behavior.sdk.trigger.segment.service;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.condition.entity.Condition;
import com.behavior.sdk.trigger.condition.repository.ConditionRepository;
import com.behavior.sdk.trigger.log_event.repository.LogEventRepository;
import com.behavior.sdk.trigger.segment.dto.SegmentCreateRequest;
import com.behavior.sdk.trigger.segment.dto.SegmentResponse;
import com.behavior.sdk.trigger.segment.entity.Segment;
import com.behavior.sdk.trigger.segment.repository.SegmentRepository;
import com.behavior.sdk.trigger.segment.repository.SegmentVisitorRepository;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SegmentServiceImpl implements SegmentService{

    private final SegmentRepository segmentRepository;
    private final SegmentVisitorRepository segmentVisitorRepository;
    private final LogEventRepository logEventRepository;
    private final VisitorRepository visitorRepository;
    private final ConditionRepository conditionRepository;

    @Override
    public Segment createSegment(SegmentCreateRequest request) {

        Condition condition = conditionRepository.findById(request.getConditionId())
                .orElseThrow(() -> new ServiceException(
                        ErrorSpec.COND_CONDITION_NOT_FOUND,
                        "존재하지 않는 조건입니다.",
                        List.of(new FieldErrorDetail("conditionId", "not found", request.getConditionId()))
                ));

        Integer threshold = condition.getThreshold();
        String pageUrl = condition.getPageUrl();

        List<UUID> visitorIds = logEventRepository.findDistinctVisitorIdsByCondition(condition.getId(), pageUrl);

        Segment segment = new Segment();
        segment.setProjectId(request.getProjectId());
        segment.setConditionId(request.getConditionId());
        segment.addVisitorsByIds(visitorIds);


        return segmentRepository.save(segment);
    }

    @Override
    public List<SegmentResponse> listSegments(UUID projectId) {
        List<Segment> segments = segmentRepository.findByProjectIdAndDeletedAtIsNull(projectId);
        return segments.stream()
                .map(s -> {
                   List<UUID> visitorIds = s.getSegmentVisitors().stream()
                           .map(segmentVisitor -> segmentVisitor.getVisitorId())
                           .toList();
                   return new SegmentResponse(
                           s.getId(),
                           s.getProjectId(),
                           s.getConditionId(),
                           visitorIds,
                           s.getDeletedAt()
                   );
                })
                .toList();
    }

    @Override
    public SegmentResponse getSegment(UUID segmentId) {
        Segment segment = segmentRepository.findById(segmentId)
                .orElseThrow(() -> new ServiceException(
                        ErrorSpec.SEG_SEGMENT_NOT_FOUND,
                        "존재하지 않는 세그먼트입니다.",
                        List.of(new FieldErrorDetail("segmentId", "not found", segmentId))
                ));
        List<UUID> visitorIds = segment.getSegmentVisitors().stream()
                .map(segmentVisitor -> segmentVisitor.getVisitorId())
                .toList();
        return new SegmentResponse(
                segment.getId(),
                segment.getProjectId(),
                segment.getConditionId(),
                visitorIds,
                segment.getDeletedAt()
        );
    }

    @Override
    public void deleteSegment(UUID segmentId) {
        Segment segment = segmentRepository.findById(segmentId)
                .orElseThrow(() -> new ServiceException(
                        ErrorSpec.SEG_SEGMENT_NOT_FOUND,
                        "존재하지 않는 세그먼트입니다.",
                        List.of(new FieldErrorDetail("segmentId", "not found", segmentId))
                ));
        segment.softDelete();
        segmentRepository.save(segment);
    }
}
