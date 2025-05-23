package com.behavior.sdk.trigger.segment.repository;

import com.behavior.sdk.trigger.segment.entity.SegmentVisitor;
import com.behavior.sdk.trigger.segment.entity.SegmentVisitorKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SegmentVisitorRepository extends JpaRepository<SegmentVisitor, SegmentVisitorKey> {
    List<SegmentVisitor> findByIdSegmentId(UUID segmentId);

   /* @Query("select sv.id.visitorId from SegmentVisitor sv where sv.id.segmentId = :segmentId")
    List<UUID> findVisitorIdsBySegmentId(@Param("segmentId") UUID segmentId);*/

    List<UUID> findIdVisitorIdByIdSegmentId(UUID segmentId);
}
