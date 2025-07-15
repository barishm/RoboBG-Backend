package com.robobg.controller;

import com.robobg.dtos.openai.ChatRequest;
import com.robobg.exceptions.RateLimitExceededException;
import com.robobg.service.impl.ChatbotServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/chatbot")
public class ChatbotController {
    private final ChatbotServiceImpl chatbotService;

    @Autowired
    public ChatbotController(ChatbotServiceImpl chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        ChatRequest response = chatbotService.getAIResponse(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    public ResponseEntity<?> rateLimitingTest(@RequestBody ChatRequest request) throws RateLimitExceededException {
        ChatRequest response = chatbotService.rateLimitingTest(request);
        return ResponseEntity.ok(response);
    }

}
