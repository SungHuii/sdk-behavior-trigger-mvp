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
            // 실제 이메일 발송
            sendGridEmailService.sendEmail(msg.getTo(), msg.getSubject(), msg.getBody());

            // 성공 -> 로그 업데이트
            emailLogService.updateEmailStatus(msg.getLogId(), EmailStatus.SENT);
            log.info("[MQ][email] sent ok: logId={}, to={}", msg.getLogId(), msg.getTo());
            status = EmailStatus.SENT;
        } catch (Exception e) {
            log.error("[MQ][email] send failed: logId={}, to={}, error={}", msg.getLogId(), msg.getTo(), e.getMessage(), e);
            // 실패 -> 로그 업데이트
            emailLogService.updateEmailStatus(msg.getLogId(), EmailStatus.FAILED);
            status = EmailStatus.FAILED;
            // 예외 던지면 requeue=false 설정 시 DLQ로 이동함
            throw e;
        }
    }
}