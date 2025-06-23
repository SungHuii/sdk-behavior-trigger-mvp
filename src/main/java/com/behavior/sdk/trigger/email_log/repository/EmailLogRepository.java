package com.behavior.sdk.trigger.email_log.repository;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {

    List<EmailLog> findAllByVisitorIdAndDeletedAtIsNull(UUID visitorId);

    boolean existsByVisitorId(UUID visitorId);

    long countByBatchIdAndStatus(UUID batchId, EmailStatus status);
}
