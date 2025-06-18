package com.behavior.sdk.trigger.email.service;

import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.dto.EmailSendResponse;
import com.behavior.sdk.trigger.email.enums.EmailStatus;
import com.behavior.sdk.trigger.email_log.entity.EmailLog;
import com.behavior.sdk.trigger.email_log.service.EmailLogService;
import com.behavior.sdk.trigger.email_template.entity.EmailTemplate;
import com.behavior.sdk.trigger.email_template.repository.EmailTemplateRepository;
import com.behavior.sdk.trigger.visitor.entity.Visitor;
import com.behavior.sdk.trigger.visitor.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender mailSender;
    private final VisitorRepository visitorRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailLogService emailLogService;

    @Override
    public EmailSendResponse sendEmail(EmailSendRequest request) {
        Visitor visitor = visitorRepository.findById(request.getVisitorId())
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 Visitor입니다."));
        String validEmail = visitor.getEmail();
        if (validEmail == null || validEmail.isEmpty()) {
            throw new IllegalArgumentException("Visitor에 유효한 이메일이 없습니다.");
        }
//      임시로 사용 X
//        EmailTemplate emailTemplate = emailTemplateRepository.findById(request.getTemplateId())
//                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 EmailTemplate입니다."));

        EmailStatus emailStatus;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(validEmail);
            message.setSubject("임시 제목");
            message.setText("임시 본문");
            mailSender.send(message);

            emailStatus = EmailStatus.SENT;
        } catch (MailException e) {
            emailStatus = EmailStatus.FAILED;
        }

//        EmailLog emailLog = emailLogService.createEmailLog(request.getVisitorId(), null, emailStatus);

        return EmailSendResponse.builder()
                .status(emailStatus)
                .build();
    }
}
