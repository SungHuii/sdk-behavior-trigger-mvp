package com.behavior.sdk.trigger.mq.consumer;

import com.behavior.sdk.trigger.config.RabbitConfig;
import com.behavior.sdk.trigger.log_event.entity.LogEvent;
import com.behavior.sdk.trigger.log_event.enums.EventType;
import com.behavior.sdk.trigger.log_event.repository.LogEventRepository;
import com.behavior.sdk.trigger.mq.dto.LogEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogEventConsumer {

    private final LogEventRepository logEventRepository;

    @RabbitListener(queues = RabbitConfig.Q_LOG_EVENTS)
    @Transactional
    public void onMessage(LogEventMessage message) {
        log.info("[MQ] consumed: {}", message);

        logEventRepository.save(LogEvent.builder()
                .projectId(message.getProjectId())
                .visitorId(message.getVisitorId())
                .eventType(EventType.valueOf(message.getEventType()))
                .pageUrl(message.getPageUrl())
                .occurredAt(message.getOccurredAt())
                .build());
    }
}
