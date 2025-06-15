package com.behavior.sdk.trigger.email_log.repository;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmailLogRepositoryImpl implements EmailLogRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void saveSentLog(UUID visitorId, UUID batchId) {

        EmailLog emailLog = EmailLog.builder()
                .id(UUID.randomUUID())
                .visitorId(visitorId)
                .batchId(batchId)
                .status(EmailStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();
        em.persist(emailLog);
    }

    @Override
    @Transactional
    public void saveFailedLog(UUID visitorId, UUID batchId, String message) {
        EmailLog emailLog = EmailLog.builder()
                .id(UUID.randomUUID())
                .visitorId(visitorId)
                .batchId(batchId)
                .status(EmailStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .errorMessage(message)
                .build();
        em.persist(emailLog);
    }

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
