package com.behavior.sdk.trigger.email.service;

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

@Service
@RequiredArgsConstructor
public class SendGridEmailService {

    @Value("${sendgrid.api-key:dummy-key}")
    private String sendGridApiKey;

    public void sendEmail(String to, String subject, String body) {
        // SendGrid API를 사용하여 이메일을 전송하는 로직을 구현합니다.
        // 예시로 SendGrid 클라이언트를 사용하여 이메일을 전송할 수 있습니다.
        // SendGrid 클라이언트 초기화 및 이메일 전송 코드 작성 필요
        Email fromEmail = new Email("noreply@visiLog.com");
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
                throw new RuntimeException("SendGrid API 요청 실패 : " + response.getBody());
            }
        } catch (IOException e) {
            throw new RuntimeException("SendGrid API 요청 중 오류 발생", e);
        }

    }
}
