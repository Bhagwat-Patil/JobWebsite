package com.jobwebsite.Service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String from, String to, String subject, String text, boolean useSuperAdmin) throws MessagingException;

    void sendEmail(String to, String subject, String body);
}
