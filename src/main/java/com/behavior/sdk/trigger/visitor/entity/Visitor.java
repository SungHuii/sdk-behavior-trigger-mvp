package com.behavior.sdk.trigger.visitor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "visitor")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Visitor {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(columnDefinition = "uuid", updatable = false, nullable = false)
   private UUID id;

   @Column(name = "project_id", columnDefinition = "uuid", nullable = false)
   private UUID projectId;

   @Column(name = "created_at", nullable = false)
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
