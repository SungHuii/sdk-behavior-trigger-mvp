package com.behavior.sdk.trigger.segment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SegmentVisitorKey implements Serializable {
    @Column(name = "segment_id", nullable = false)
    private UUID segmentId;

    @Column(name = "visitor_id", nullable = false)
    private UUID visitorId;
}
