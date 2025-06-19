package com.behavior.sdk.trigger.email.service;

import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.dto.EmailSendResponse;
import com.behavior.sdk.trigger.email.enums.EmailStatus;
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

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmailServiceImpl implements EmailService{

    private final VisitorRepository visitorRepository;
//    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailLogService emailLogService;

    @Value("${sendgrid.api-key:dummy-key}")
    private String sendGridApiKey;

    @Override
    public EmailSendResponse sendEmail(EmailSendRequest request) {

        Visitor visitor = visitorRepository.findById(request.getVisitorId())
                .orElseThrow(() -> new EntityNotFoundException("방문자를 찾을 수 없습니다."));

        String validEmail = visitor.getEmail();
        if (validEmail == null || validEmail.isEmpty()) {
            throw new IllegalArgumentException("유효한 이메일 주소가 필요합니다.");
        }

        EmailStatus emailStatus;
        try {
            sendWithSendGrid(validEmail, "임시 제목", "임시 내용");
            emailStatus = EmailStatus.SENT;
        } catch (Exception e) {
            log.error("SendGrid 이메일 전송 실패: {}", e.getMessage());
            emailStatus = EmailStatus.FAILED;
        }

        EmailLog emailLog = emailLogService.createEmailLog(request.getVisitorId(), null, emailStatus);

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
            throw new RuntimeException("SendGrid API 요청 실패: " + response.getBody());
        }
    }
}
