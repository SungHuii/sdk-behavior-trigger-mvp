package com.behavior.sdk.trigger.email_log.repository;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
