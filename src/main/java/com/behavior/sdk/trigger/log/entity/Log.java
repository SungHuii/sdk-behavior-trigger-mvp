package com.behavior.sdk.trigger.log.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "log")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Log {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(columnDefinition = "uuid", updatable = false, nullable = false)
   private UUID id;

   @Column(name="project_id", nullable = false, columnDefinition = "uuid")
   private UUID projectId;

   @Column(name="visitor_id", nullable = false, columnDefinition = "uuid")
   private UUID visitorId;

   @Column(name="event_type", nullable = false)
   private String eventType;

   @Column(name="duration_ms")
   private Long durationMs;

   @Column(name="occurred_at", nullable = false)
   private LocalDateTime occurredAt;

   @Column(name="created_at", nullable = false)
   private LocalDateTime createdAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
   }
}
