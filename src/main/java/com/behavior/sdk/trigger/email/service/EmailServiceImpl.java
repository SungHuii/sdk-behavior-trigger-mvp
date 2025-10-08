package com.behavior.sdk.trigger.email.service;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.dto.EmailSendResponse;
import com.behavior.sdk.trigger.email.enums.EmailStatus;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailServiceImpl implements EmailService{

    private final VisitorRepository visitorRepository;
//    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailLogService emailLogService;
    private final SendGridEmailService sendGridEmailService;

    @Value("${sendgrid.api-key:dummy-key}")
    private String sendGridApiKey;

    @Override
    public EmailSendResponse sendEmail(EmailSendRequest request) {

        var visitor = visitorRepository.findById(request.getVisitorId())
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

        EmailStatus emailStatus;
        try {
            sendGridEmailService.sendText(fromEmail.address(), fromEmail.name(), validEmail, subject, body);
            emailStatus = EmailStatus.SENT;
        } catch (Exception e) {
            log.error("SendGrid 이메일 전송 실패: {}", e.getMessage());
            emailStatus = EmailStatus.FAILED;
        }

        var emailLog = emailLogService.createEmailLog(request.getVisitorId(), null, emailStatus);

        return EmailSendResponse.builder()
                .logId(emailLog.getId())
                .status(emailStatus)
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
