package com.robobg.controller;


import com.robobg.entity.dtos.ContactUsFormDTO;
import com.robobg.service.ContactUsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/contact-us")
public class ContactUsController {
    @Autowired
    private ContactUsService contactUsService;

    @PostMapping
    public ResponseEntity<String> handleFormSubmission(@Valid @RequestBody ContactUsFormDTO contactUsFormDTO) {
        contactUsService.handleContactUsForm(contactUsFormDTO);
        return ResponseEntity.ok().build();
    }
}
