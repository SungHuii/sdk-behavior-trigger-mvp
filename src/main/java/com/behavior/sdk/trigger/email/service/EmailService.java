package com.behavior.sdk.trigger.email.service;

import com.behavior.sdk.trigger.email.dto.EmailSendRequest;
import com.behavior.sdk.trigger.email.dto.EmailSendResponse;

public interface EmailService {

    EmailSendResponse sendEmail(EmailSendRequest request);

}

