package com.behavior.sdk.trigger.email.service;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.dto.EmailSendResponse;
import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email.messaging.dto.EmailSendMessage;
import com.behavior.sdk.trigger.email.messaging.producer.EmailSendProducer;
import com.behavior.sdk.trigger.email.support.DisplayNameResolver;
import com.behavior.sdk.trigger.email.support.FromResolver;
import com.behavior.sdk.trigger.email.template.SimpleTemplates;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import com.behavior.sdk.trigger.email_log.service.EmailLogService;
import com.behavior.sdk.trigger.visitor.entity.Visitor;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailServiceImpl implements EmailService{

    private final VisitorRepository visitorRepository;
//    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailLogService emailLogService;
    private final EmailSendProducer emailSendProducer;

    @Value("${sendgrid.api-key:dummy-key}")
    private String sendGridApiKey;

    @Override
    public EmailSendResponse sendEmail(EmailSendRequest request) {

        // 방문자 조회 + 검증
        Visitor visitor = visitorRepository.findById(request.getVisitorId())
                .orElseThrow(() -> new ServiceException(
                        ErrorSpec.VALID_PARAM_VALIDATION_FAILED,
                        "존재하지 않는 방문자입니다.",
                        List.of(new FieldErrorDetail("visitorId", "not found", request.getVisitorId()))
                ));

        String validEmail = visitor.getEmail();
        if (validEmail == null || validEmail.isEmpty()) {
            throw new ServiceException(
                    ErrorSpec.VALID_PARAM_VALIDATION_FAILED,
                    "방문자에 이메일이 존재하지 않습니다.",
                    List.of(new FieldErrorDetail("visitorId", "email not found", request.getVisitorId()))
            );
        }

        // 표시 이름 : 이메일 로컬 파트 기반
        String displayName = DisplayNameResolver.fromEmailLocalPart(validEmail);

        // From 결정 (프로젝트 기반)
        var fromEmail = FromResolver.resolve(null /*projectName*/, null /*allowedDomain*/);

        // 하드코딩 템플릿 적용
        String subject = SimpleTemplates.subjectForGreeting(displayName);
        String body = SimpleTemplates.bodyForGreetingText(displayName);

        // EmailLog를 먼저 QUEUED로 생성 -> logId 획득
        EmailLog queuedLog = emailLogService.createEmailLog(
                request.getVisitorId(),
                null, // 템플릿 비활성화 상태
                EmailStatus.QUEUED
        );

        // MQ 메시지 구성, 발행 (컨슈머가 실제 발송 후 로그 상태를 SENT/FAILED로 업데이트)
        EmailSendMessage msg = EmailSendMessage.builder()
                .logId(queuedLog.getId()) // 나중에 상태 업데이트
                .projectId(visitor.getProjectId())
                .visitorId(visitor.getId())
                .to(validEmail)
                .subject(subject)
                .body(body)
                .templateId(null) // 템플릿 비활성화 상태
                .requestedAt(LocalDateTime.now())
                .dedupKey(visitor.getId()+":"+System.currentTimeMillis())
                .build();

        emailSendProducer.publish(msg);
        log.info("[EmailService] queued email: logId={}, to={}, fromName={}, fromAddr={}",
                queuedLog.getId(), validEmail, fromEmail.name(), fromEmail.address());

        // 응답 : 접수(QUEUED) 상태로 반환
        return EmailSendResponse.builder()
                .logId(queuedLog.getId())
                .status(EmailStatus.QUEUED)
                .build();
    }

    public void sendWithSendGrid(String to, String subject, String body) throws IOException {
        Email fromEmail = new Email("gkemg2017@gmail.com");
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);

        if (response.getStatusCode() >= 400) {
            throw new ServiceException(
                    ErrorSpec.MAIL_EMAIL_PROVIDER_ERROR,
                    "SendGrid API 요청 실패 : " + response.getBody(),
                    List.of(
                            new FieldErrorDetail("provider", "error status", response.getStatusCode()),
                            new FieldErrorDetail("body", "upstream error", response.getBody())
                    )
            );
        }
    }
}
