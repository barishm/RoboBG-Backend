package com.robobg.service.impl;

import com.robobg.entity.dtos.ContactUsFormDTO;
import com.robobg.service.ContactUsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactUsServiceImpl implements ContactUsService {

    private final EmailService emailService;

    @Override
    public void handleContactUsForm(ContactUsFormDTO contactUsFormDTO) {
        emailService.sendContactUsEmail(contactUsFormDTO);
    }
}
