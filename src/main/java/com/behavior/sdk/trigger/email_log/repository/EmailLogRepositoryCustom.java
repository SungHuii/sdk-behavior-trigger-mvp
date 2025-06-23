package com.behavior.sdk.trigger.email_log.repository;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailLogRepositoryCustom {

    long countByBatchIdAndStatus(UUID batchId, EmailStatus status);
}
