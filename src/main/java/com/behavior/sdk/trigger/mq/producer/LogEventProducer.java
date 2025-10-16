/*
package com.behavior.sdk.trigger.mq.producer;

import com.behavior.sdk.trigger.config.RabbitConfig;
import com.behavior.sdk.trigger.mq.dto.LogEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publish(LogEventMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EX_LOG,
                RabbitConfig.RK_LOG_CREATED,
                message
        );
        log.info("[MQ] published : {}", message);
    }
}
*/
