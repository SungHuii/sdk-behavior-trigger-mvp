package com.behavior.sdk.trigger.email.messaging.consumer;

import com.behavior.sdk.trigger.config.RabbitConfig;
import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email.messaging.dto.EmailSendMessage;
import com.behavior.sdk.trigger.email.service.SendGridEmailService;
import com.behavior.sdk.trigger.email_log.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSendConsumer {

    private final SendGridEmailService sendGridEmailService;
    private final EmailLogService emailLogService;

    @RabbitListener(queues = RabbitConfig.Q_EMAIL_SEND)
    @Transactional
    public void onEmailSend(EmailSendMessage msg) {
        log.info("[MQ][email] consumed: {}", msg);

        EmailStatus status = null;
        try {
            sendGridEmailService.sendEmail(msg.getTo(), msg.getSubject(), msg.getBody());
            status = EmailStatus.SENT;
        } catch (Exception e) {
            log.error("[MQ][email] send failed: {}", e.getMessage(), e);
            status = EmailStatus.FAILED;
            // 예외 던지면 requeue=false 설정 시 DLQ로 이동함
            throw e;
        } finally {
            // 로그 발송 성공 / 실패 모두 기록
            emailLogService.createEmailLog(msg.getVisitorId(), msg.getTemplateId(), status);
        }
    }
}
