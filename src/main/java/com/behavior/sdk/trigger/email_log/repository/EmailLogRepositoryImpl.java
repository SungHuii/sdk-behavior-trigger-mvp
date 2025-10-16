package com.behavior.sdk.trigger.email_log.repository;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EmailLogRepositoryImpl implements EmailLogRepositoryCustom {
    @PersistenceContext
    private EntityManager em;


    @Override
    public long countByBatchIdAndStatus(UUID batchId, EmailStatus status) {
        return em.createQuery("""
                SELECT COUNT(e)
                FROM EmailLog e
                WHERE e.batchId = :batchId AND e.status = :status
                """, Long.class)
                .setParameter("batchId", batchId)
                .setParameter("status", status)
                .getSingleResult();
    }
}
