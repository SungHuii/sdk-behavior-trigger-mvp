package com.behavior.sdk.trigger.email_log.repository;

import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {

    List<EmailLog> findAllByVisitorIdAndDeletedAtIsNull(UUID visitorId);

    boolean existsByVisitorIdAndTemplateId(UUID visitorId);
}
