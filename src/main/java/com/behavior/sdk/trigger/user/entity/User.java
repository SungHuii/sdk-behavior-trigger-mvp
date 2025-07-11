package com.behavior.sdk.trigger.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    protected void onCreated() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    // --- UserDetails 인터페이스 구현 메서드 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 필요에 따라 ROLE_USER 같은 권한을 반환할 수 있음
        return List.of();  // 예: List.of(new SimpleGrantedAuthority("ROLE_USER"))
    }

    @Override
    public String getUsername() {
        return this.email;  // email을 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 만료 정책 없으면 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠금 정책 없으면 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 정책 없으면 true
    }

    @Override
    public boolean isEnabled() {
        return this.deletedAt == null; // soft delete 된 계정은 비활성화
    }
}
