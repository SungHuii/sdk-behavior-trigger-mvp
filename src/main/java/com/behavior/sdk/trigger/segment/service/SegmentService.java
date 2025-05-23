package com.behavior.sdk.trigger.segment.service;

import com.behavior.sdk.trigger.segment.dto.SegmentCreateRequest;
import com.behavior.sdk.trigger.segment.dto.SegmentResponse;
import com.behavior.sdk.trigger.segment.entity.Segment;

import java.util.List;
import java.util.UUID;

public interface SegmentService {

    Segment createSegment(SegmentCreateRequest request);

    List<SegmentResponse> listSegments(UUID projectId);

    SegmentResponse getSegment(UUID segmentId);

    void deleteSegment(UUID segmentId);
}
