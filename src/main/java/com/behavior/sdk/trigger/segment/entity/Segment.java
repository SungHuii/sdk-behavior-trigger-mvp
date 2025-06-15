package com.behavior.sdk.trigger.segment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "segment")
public class Segment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, columnDefinition = "uuid")
    private UUID projectId;

    @Column(name = "condition_id", nullable = false, columnDefinition = "uuid")
    private UUID conditionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public void markAsProcessed() {
        this.processedAt = LocalDateTime.now();
    }

    @Builder.Default
    @OneToMany(mappedBy = "segment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SegmentVisitor> segmentVisitors = new HashSet<>();

    @PrePersist
    public void onCreated() {
        this.createdAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void addVisitorsByIds(List<UUID> visitorIds) {
        visitorIds.forEach(visitorId -> {
            SegmentVisitor sv = new SegmentVisitor(this, visitorId);
            this.segmentVisitors.add(sv);
        });
    }
}
