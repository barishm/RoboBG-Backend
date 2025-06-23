package com.robobg.service;

import com.robobg.dtos.ContactUsFormDTO;
import org.springframework.stereotype.Service;

@Service
public interface ContactUsService {
    void handleContactUsForm(ContactUsFormDTO contactUsFormDTO);
}
