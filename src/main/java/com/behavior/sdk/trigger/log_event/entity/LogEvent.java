package com.behavior.sdk.trigger.log_event.entity;

import com.behavior.sdk.trigger.condition.entity.Condition;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "log_event")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LogEvent {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(columnDefinition = "uuid", updatable = false, nullable = false)
   private UUID id;

   @Column(name="project_id", nullable = false, columnDefinition = "uuid")
   private UUID projectId;

   @Column(name="visitor_id", nullable = false, columnDefinition = "uuid")
   private UUID visitorId;

   @ManyToOne(optional = false)
   @JoinColumn(name = "condition_id", nullable = true)
   private Condition condition;

   @Enumerated(EnumType.STRING)
   @Column(name="event_type", nullable = false)
   private EventType eventType;

   @Column(name="occurred_at", nullable = false)
   private LocalDateTime occurredAt;

   @Column(name="created_at", nullable = false)
   private LocalDateTime createdAt;

   @Column(name="deleted_at")
   private LocalDateTime deletedAt;

   @Column(name="page_url", nullable = false, length = 2048)
   private String pageUrl;


   @PrePersist
   public void onCreated() {
      this.createdAt = LocalDateTime.now();
   }

   public void softDelete() {
      this.deletedAt = LocalDateTime.now();
   }

}
