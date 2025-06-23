package com.robobg.service.impl;

import com.robobg.dtos.ContactUsFormDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${application.frontend.reset-password-url}")
    private String resetPasswordUrl;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Autowired
    private JavaMailSender mailSender;
    private static final String[] RECIPIENTS = {
            "barishm1337@gmail.com",
            "eshk088@gmail.com"
    };

    @Async
    public void sendPasswordResetEmail(String recipientEmail, String token) {
        try {
            String resetLink = resetPasswordUrl + "?token=" + token;

            String htmlContent = buildResetEmailHtml(resetLink);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Password Reset Request");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send password reset email", e);
        }
    }

    @Async
    public void sendContactUsEmail(ContactUsFormDTO contactUsFormDTO) {
        try {
            String subject = "New Contact Us Form Submission";
            String textContent = String.format(
                    "Name: %s%nEmail: %s%nMessage:%n%s",
                    contactUsFormDTO.getName(),
                    contactUsFormDTO.getEmail(),
                    contactUsFormDTO.getMessage()
            );

            sendTextEmail(RECIPIENTS, subject, textContent);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send contact us email", e);
        }
    }

    private void sendTextEmail(String[] to, String subject, String textContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(textContent, false);
        mailSender.send(message);
    }

    private String buildResetEmailHtml(String resetLink) {
        return "<!DOCTYPE html>" +
                "<html style='font-family: Arial, sans-serif;'>" +
                "<body style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h2 style='color: #2c3e50;'>Password Reset Request</h2>" +
                "<p>We received a request to reset your password. Click the button below to proceed:</p>" +
                "<a href='" + resetLink + "' style='" +
                "display: inline-block; padding: 10px 20px; " +
                "background-color: #3498db; color: white; " +
                "text-decoration: none; border-radius: 4px;'>" +
                "Reset Password</a>" +
                "<p style='margin-top: 20px;'>If you didn't request this, please ignore this email.</p>" +
                "<p style='color: #7f8c8d; font-size: 0.9em;'>" +
                "This link will expire in 1 hour.</p>" +
                "</body></html>";
    }

    public static class EmailSendingException extends RuntimeException {
        public EmailSendingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
