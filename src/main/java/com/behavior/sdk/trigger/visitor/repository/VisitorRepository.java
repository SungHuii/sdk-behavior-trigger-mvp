package com.behavior.sdk.trigger.visitor.repository;

import com.behavior.sdk.trigger.visitor.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitorRepository extends JpaRepository<Visitor, UUID> {


    boolean existsByIdAndDeletedAtIsNull(UUID id);

    @Query("""
        SELECT DISTINCT v.email
        FROM Visitor v
        WHERE v.id IN :visitorIds
        AND v.email IS NOT NULL
    """)
    List<String> findDistinctEmailsByVisitorIds(@Param("visitorIds") List<UUID> visitorIds);
}
