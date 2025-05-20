package com.behavior.sdk.trigger.segment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "segment_visitors")
public class SegmentVisitor {

    @EmbeddedId
    private SegmentVisitorKey id;

    @Embeddable
    public static class SegmentVisitorKey implements Serializable {
        @Column(name = "segment_id", nullable = false)
        private Long segmentId;

        @Column(name = "visitor_id", nullable = false)
        private Long visitorId;

        // equals() and hashCode() methods
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SegmentVisitorKey)) return false;
            SegmentVisitorKey that = (SegmentVisitorKey) o;
            return segmentId.equals(that.segmentId) && visitorId.equals(that.visitorId);
        }

        @Override
        public int hashCode() {
            return 31 * segmentId.hashCode() + visitorId.hashCode();
        }
    }
}
