package com.behavior.sdk.trigger.segment.controller;

import com.behavior.sdk.trigger.segment.dto.EmailBatchResponse;
import com.behavior.sdk.trigger.segment.dto.SegmentEmailSendRequest;
import com.behavior.sdk.trigger.segment.service.SegmentEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/segments")
@RequiredArgsConstructor
public class SegmentEmailController {

    private final SegmentEmailService segmentEmailService;

    @PostMapping("/{segmentId}/send-email")
    public ResponseEntity<EmailBatchResponse> sendEmailToSegment(
            @PathVariable UUID segmentId,
            @RequestBody @Valid SegmentEmailSendRequest emailSendRequest) {
        EmailBatchResponse emailBatchResponse = segmentEmailService
                .sendEmailBatch(segmentId, emailSendRequest.getTemplateId());

        return ResponseEntity.ok(emailBatchResponse);
    }
}
