package com.behavior.sdk.trigger.mq.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEventMessage {

    private UUID projectId;
    private UUID visitorId;
    private String eventType;
    private String pageUrl;
    private LocalDateTime occurredAt;
}
