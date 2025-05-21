package com.behavior.sdk.trigger.segment.repository;

import com.behavior.sdk.trigger.segment.entity.SegmentVisitor;
import com.behavior.sdk.trigger.segment.entity.SegmentVisitorKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SegmentVisitorRepository extends JpaRepository<SegmentVisitor, SegmentVisitorKey> {
    List<SegmentVisitor> findByIdSegmentId(UUID segmentId);
}
