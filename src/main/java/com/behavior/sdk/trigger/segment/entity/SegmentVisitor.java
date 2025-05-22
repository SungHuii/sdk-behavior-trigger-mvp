package com.behavior.sdk.trigger.segment.entity;

import com.behavior.sdk.trigger.visitor.entity.Visitor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "segment_visitors")
public class SegmentVisitor {

    @EmbeddedId
    private SegmentVisitorKey id;

    @MapsId("segmentId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id", nullable = false)
    private Segment segment;

//    @MapsId("visitorId")
    @Column(name = "visitor_id", nullable = false, insertable = false, updatable = false)
    private UUID visitorId;


    public SegmentVisitor(Segment segment, UUID visitorId) {
        this.segment = segment;
        this.visitorId = visitorId;
        this.id = new SegmentVisitorKey(segment.getId(), visitorId);
    }
}
