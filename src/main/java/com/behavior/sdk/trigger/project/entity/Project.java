package com.behavior.sdk.trigger.project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Project {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(columnDefinition = "uuid", updatable = false, nullable = false)
   private UUID id;

   @Column(nullable = false)
   private String name;

   @Column(name = "sdk_key", unique = true, nullable = false)
   private String sdkKey;

   @Column(name = "owner_id", nullable = false)
   private UUID ownerId;

   @Column(name = "created_at", nullable = false)
   private LocalDateTime createdAt;

   @Column(name = "updated_at")
   private LocalDateTime updatedAt;

   @Column(name = "deleted_at")
   private LocalDateTime deletedAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
      if (this.sdkKey == null) {
         this.sdkKey = UUID.randomUUID().toString();
      }
   }

   @PreUpdate
   protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
   }

   public void softDelete() {
      this.deletedAt = LocalDateTime.now();
   }


}
