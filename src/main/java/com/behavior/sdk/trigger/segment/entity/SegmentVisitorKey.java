package com.behavior.sdk.trigger.segment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
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

/*    // equals() and hashCode() methods
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
    }*/
}
