package com.robobg.service.impl;

import com.robobg.entity.dtos.ContactUsFormDTO;
import com.robobg.service.ContactUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ContactUsServiceImpl implements ContactUsService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String[] RECIPIENTS = {
            "barishm1337@gmail.com",
            "eshk088@gmail.com"
    };

    @Override
    public void handleContactUsForm(ContactUsFormDTO contactUsFormDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("New Contact Us Form Submission");

        String emailContent = String.format(
                "Name: %s%nEmail: %s%nMessage:%n%s",
                contactUsFormDTO.getName(),
                contactUsFormDTO.getEmail(),
                contactUsFormDTO.getMessage()
        );

        message.setText(emailContent);
        message.setFrom("barishm1337@gmail.com");
        message.setTo(RECIPIENTS);

        mailSender.send(message);
    }
}
