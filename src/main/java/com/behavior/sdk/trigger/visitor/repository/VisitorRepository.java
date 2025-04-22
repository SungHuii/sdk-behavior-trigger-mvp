package com.behavior.sdk.trigger.visitor.repository;

import com.behavior.sdk.trigger.visitor.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VisitorRepository extends JpaRepository<Visitor, UUID> {

    Optional<Visitor> findByVisitorKeyAndDeletedAtIsNull(String visitorKey);
}
