package com.behavior.sdk.trigger.segment.repository;

import com.behavior.sdk.trigger.segment.entity.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SegmentRepository extends JpaRepository<Segment, UUID> {
    List<Segment> findByProjectIdAndDeletedAtIsNull(UUID projectId);

    @Query("select v.visitorId from SegmentVisitor v where v.segment.id = :segmentId")
    List<UUID> findVisitorIdsBySegmentId(@Param("segmentId") UUID segmentId);

    @Query("""
            SELECT count(s) > 0
            FROM Segment s
            WHERE s.conditionId = :conditionId
            AND s.deletedAt IS NULL
            """)
    boolean existsByConditionIdAndNotDeletedAtIsNull(@Param("conditionId") UUID conditionId);
}
