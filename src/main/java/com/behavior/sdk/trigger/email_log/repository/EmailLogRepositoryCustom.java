package com.behavior.sdk.trigger.email_log.repository;

import com.behavior.sdk.trigger.email.enums.EmailStatus;

import java.util.UUID;

public interface EmailLogRepositoryCustom {

    void saveSentLog(UUID visitorId, UUID batchId);

    void saveFailedLog(UUID visitorId, UUID batchId, String message);

    long countByBatchIdAndStatus(UUID batchId, EmailStatus status);
}
