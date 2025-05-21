package com.behavior.sdk.trigger.segment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@IdClass(SegmentVisitorKey.class)
@Table(name = "segment_visitors")
public class SegmentVisitor {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id", nullable = false, columnDefinition = "uuid")
    private Segment segment;

    @Id
    @Column(name = "visitor_id", nullable = false, columnDefinition = "uuid")
    private UUID visitorId;


    public SegmentVisitor(Segment segment, UUID visitorId) {
        this.segment = segment;
        this.visitorId = visitorId;
    }
}
