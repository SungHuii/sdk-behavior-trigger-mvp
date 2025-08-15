package com.behavior.sdk.trigger.condition.entity;

import com.behavior.sdk.trigger.log_event.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "conditions")
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type",nullable = false)
    private EventType eventType;

    @Column(name = "operator", nullable = false)
    private String operator;

    @Column(name = "threshold", nullable = false)
    private Integer threshold;

    @Column(name = "page_url", nullable = false)
    private String pageUrl;

    @Column(name = "segment_min_emails")
    private Integer segmentMinEmails;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
