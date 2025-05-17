package com.behavior.sdk.trigger.email_log.service;

import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;

import java.util.List;
import java.util.UUID;

public interface EmailLogService {

    EmailLog createEmailLog(UUID visitorId, UUID templateId, EmailStatus status);

    List<EmailLog> listLogsByVisitorId(UUID visitorId);

    void softDeleteEmailLog(UUID emailLogId);
}
