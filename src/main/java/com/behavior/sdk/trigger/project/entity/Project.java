package com.behavior.sdk.trigger.project.entity;

import com.behavior.sdk.trigger.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
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
   private UUID id;  // SDK 키 겸용

   @Column(nullable = false)
   private String name;

   @ElementCollection(fetch = FetchType.EAGER)
   @CollectionTable(
           name = "project_allowed_domains",
           joinColumns = @JoinColumn(name = "project_id")
   )
   @Column(name = "domain")
   private List<String> allowedDomains; // 여러 도메인 허용 가능

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

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
