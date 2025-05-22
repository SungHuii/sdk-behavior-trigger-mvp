package com.behavior.sdk.trigger.segment.repository;

import com.behavior.sdk.trigger.segment.entity.EmailBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailBatchRepository extends JpaRepository<EmailBatch, UUID> {
}
