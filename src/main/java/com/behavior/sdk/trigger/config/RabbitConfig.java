package com.behavior.sdk.trigger.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit // RabbitMQ 관련 어노테이션 활성화
public class RabbitConfig {

    public static final String EX_LOG = "ex.log";
    public static final String RK_LOG_CREATED = "log.created";
    public static final String Q_LOG_EVENTS = "q.log.events";

    public static final String Q_LOG_EVENTS_DLQ = "q.log.events.dlq";
    public static final String RK_LOG_EVENTS_DLQ = "log.events.dlq";

    // 이메일 발송 관련
    public static final String EX_EMAIL = "ex.email";
    public static final String RK_EMAIL_SEND = "email.send";
    public static final String Q_EMAIL_SEND = "q.email.send";
    public static final String Q_EMAIL_SEND_DLQ = "q.email.send.dlq";
    public static final String RK_EMAIL_SEND_DLQ = "email.send.dlq";

    @Bean
    public TopicExchange logExchange() {
        return ExchangeBuilder.topicExchange(EX_LOG).durable(true).build();
    }

    @Bean
    public Queue logEventsQueue() {
        return QueueBuilder.durable(Q_LOG_EVENTS)
                .withArgument("x-dead-letter-exchange", EX_LOG)
                .withArgument("x-dead-letter-routing-key", RK_LOG_EVENTS_DLQ)
                .build();
    }

    @Bean
    public Binding logEventsBinding() {
        return BindingBuilder.bind(logEventsQueue())
                .to(logExchange()).with(RK_LOG_CREATED);
    }

    @Bean
    public Queue logEventsDlq() {
        return QueueBuilder.durable(Q_LOG_EVENTS_DLQ).build();
    }

    @Bean
    public Binding logEventsDlqBinding() {
        return BindingBuilder.bind(logEventsDlq())
                .to(logExchange()).with(RK_LOG_EVENTS_DLQ);
    }

    // 메시지(JSON) 직렬화 / 역직렬화
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 이메일 발송 관련 Beans
    @Bean
    public TopicExchange emailExchange() {
        return ExchangeBuilder.topicExchange(EX_EMAIL).durable(true).build();
    }

    @Bean
    public Queue emailSendQueue() {
        return QueueBuilder.durable(Q_EMAIL_SEND)
                .withArgument("x-dead-letter-exchange", EX_EMAIL)
                .withArgument("x-dead-letter-routing-key", RK_EMAIL_SEND_DLQ)
                .build();
    }

    @Bean
    public Binding emailSendBinding() {
        return BindingBuilder.bind(emailSendQueue())
                .to(emailExchange()).with(RK_EMAIL_SEND);
    }

    @Bean
    public Queue emailSendDlq() {
        return QueueBuilder.durable(Q_EMAIL_SEND_DLQ).build();
    }

    @Bean
    public Binding emailSendDlqBinding() {
        return BindingBuilder.bind(emailSendDlq())
                .to(emailExchange()).with(RK_EMAIL_SEND_DLQ);
    }
}
