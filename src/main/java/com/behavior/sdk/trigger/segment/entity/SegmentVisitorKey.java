package com.behavior.sdk.trigger.segment.entity;

import jakarta.persistence.Column;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class SegmentVisitorKey implements Serializable {
    @Column(name = "segment_id", nullable = false)
    private UUID segmentId;

    @Column(name = "visitor_id", nullable = false)
    private UUID visitorId;

    public SegmentVisitorKey() {
    }

    public SegmentVisitorKey(UUID segmentId, UUID visitorId) {
        this.segmentId = segmentId;
        this.visitorId = visitorId;
    }

    // equals() and hashCode() methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SegmentVisitorKey)) return false;
        SegmentVisitorKey that = (SegmentVisitorKey) o;
        return Objects.equals(segmentId, that.segmentId) && Objects.equals(visitorId, that.visitorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segmentId, visitorId);
    }
}
