package com.behavior.sdk.trigger.segment.repository;

import com.behavior.sdk.trigger.segment.entity.SegmentVisitor;
import com.behavior.sdk.trigger.segment.entity.SegmentVisitorKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SegmentVisitorRepository extends JpaRepository<SegmentVisitor, SegmentVisitorKey> {

    /*static void saveByIds(UUID segmentId, UUID id) {
    }*/

    @Query("""
      select sv.visitorId
        from SegmentVisitor sv
       where sv.segment.id = :segmentId
    """)
    List<UUID> findVisitorIdsBySegmentId(@Param("segmentId") UUID segmentId);}
