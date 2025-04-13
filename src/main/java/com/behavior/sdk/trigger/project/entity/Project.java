package com.behavior.sdk.trigger.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Project {

   @Id
   @GeneratedValue()
   @Column(columnDefinition = "uuid", updatable = false, nullable = false)
   private UUID id;

   @Column(nullable = false)
   private String name;

   @Column(name = "api_key", unique = true, nullable = false)
   private String apiKey;

   @Column(name = "created_at", nullable = false)
   private LocalDateTime createdAt;

   @Column(name = "deleted_at")
   private LocalDateTime deletedAt;

   @PrePersist
   protected void onCreate() {
      this.createdAt = LocalDateTime.now();
      if (this.apiKey == null) {
         this.apiKey = UUID.randomUUID().toString();
      }
   }


}
