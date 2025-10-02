package com.behavior.sdk.trigger.email.service;

import com.behavior.sdk.trigger.common.exception.ErrorSpec;
import com.behavior.sdk.trigger.common.exception.FieldErrorDetail;
import com.behavior.sdk.trigger.common.exception.ServiceException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SendGridEmailService {

    @Value("${sendgrid.api-key:dummy-key}")
    private String sendGridApiKey;

    public void sendText(String fromAddress, String fromName,
                         String to, String subject, String body) {

        Email from = new Email(fromAddress, fromName);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toEmail, content);
    }

    public void sendEmail(String to, String subject, String body) {
        Email fromEmail = new Email("gkemg2017@gmail.com");
        Email toEmail = new Email(to);

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                throw new ServiceException(
                        ErrorSpec.MAIL_EMAIL_PROVIDER_ERROR,
                        "SendGrid API 요청 실패",
                        List.of(
                                new FieldErrorDetail("provider", "error status", response.getStatusCode()),
                                new FieldErrorDetail("body", "upstream response", response.getBody())
                        )
                );
            }
        } catch (IOException e) {
            throw new ServiceException(
                    ErrorSpec.MAIL_EMAIL_PROVIDER_ERROR,
                    "SendGrid API 요청 중 오류 발생",
                    List.of(new FieldErrorDetail("exception", "io exception", e.getClass().getSimpleName()))
            );
        }

    }
}
