package com.behavior.sdk.trigger.segment.service;

import com.behavior.sdk.trigger.segment.dto.EmailBatchResponse;

import java.util.UUID;

public interface SegmentEmailService {
    EmailBatchResponse sendEmailBatch(UUID segmentId, UUID templateId);
}
