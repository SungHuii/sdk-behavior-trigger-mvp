package com.behavior.sdk.trigger.segment.repository;

import com.behavior.sdk.trigger.segment.entity.Segment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SegmentRepository extends JpaRepository<Segment, UUID> {
    List<Segment> findByProjectIdAndDeletedAtIsNull(UUID projectId);
}
