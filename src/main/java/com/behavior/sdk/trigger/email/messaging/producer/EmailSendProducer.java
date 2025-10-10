package com.behavior.sdk.trigger.email.messaging.producer;

import com.behavior.sdk.trigger.config.RabbitConfig;
import com.behavior.sdk.trigger.email.messaging.dto.EmailSendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSendProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publish(EmailSendMessage msg) {
        rabbitTemplate.convertAndSend(RabbitConfig.EX_EMAIL, RabbitConfig.RK_EMAIL_SEND, msg);
        log.info("[MQ][email] published : {}", msg);
    }
}
