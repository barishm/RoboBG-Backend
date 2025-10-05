package com.robobg.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robobg.dtos.openai.ChatMessageDTO;
import com.robobg.dtos.openai.ChatRequest;
import io.github.bucket4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.Duration.*;


@Service
public class ChatbotServiceImpl {

    private final RestTemplate restTemplate;
    private final Bucket rateLimiter;
    private static final int MAX_MESSAGES = 10;
    private static final int MAX_MESSAGE_CHARACTERS = 300;
    private static final int MAX_TOKEN_USAGE_BY_API = 300;
    private static final int RATE_LIMITING_TOKEN_CAPACITY = 1000;
    private static final int RATE_LIMITING_REFILL_COUNT = 100;
    private static final String developerPrompt = "You are a helpful assistant of vacuum cleaning robots website that speaks Bulgarian and helps users";
    private static final String ASSISTANT_SUPPORT_MESSAGE =
            "Моля свържете се с екипа на support@example.com или чрез формата за контакт тук за повече информация.";



    @Value("${OPENAI_API_KEY}")
    private String openaiApiKey;


    @Autowired
    public ChatbotServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        Instant firstRefillTime = ZonedDateTime.now()
                .truncatedTo(ChronoUnit.HOURS).plusHours(1)
                .toInstant();


        this.rateLimiter = Bucket.builder().addLimit(limit ->
                limit.capacity(RATE_LIMITING_TOKEN_CAPACITY).refillIntervallyAligned(RATE_LIMITING_REFILL_COUNT, ofHours(1), firstRefillTime)).build();


    }

    public ChatRequest rateLimitingTest(ChatRequest chatRequest) {
        if (rateLimiter.tryConsume(1)) {
            chatRequest.addMessage("assistant", "Token consumed, you're good to go!");
        } else {
            chatRequest.addMessage("assistant", "Всички дневни заявки са изчерпани. Моля, опитайте отново по-късно.");
        }

        return chatRequest;
    }

    public ChatRequest getAIResponse(ChatRequest chatRequest) {
        ensureDeveloperPrompt(chatRequest);

        if (!validateRequest(chatRequest)) {
            return chatRequest;
        }

        HttpEntity<Map<String, Object>> request = buildOpenAIRequest(chatRequest.getMessages());
        ResponseEntity<String> response = callOpenAI(request);

        addAssistantOrFallback(chatRequest, parseOpenAIResponse(response));
        return chatRequest;
    }

    private void ensureDeveloperPrompt(ChatRequest chatRequest) {
        boolean hasDeveloper = chatRequest.getMessages().stream()
                .anyMatch(m -> "developer".equals(m.getRole()));
        if (!hasDeveloper) {
            chatRequest.getMessages().add(0, new ChatMessageDTO("developer", developerPrompt));
        }
    }

    private boolean validateRequest(ChatRequest chatRequest) {
        List<ChatMessageDTO> messages = chatRequest.getMessages();

        if (!rateLimiter.tryConsume(1)) {
            chatRequest.addMessage("assistant", ASSISTANT_SUPPORT_MESSAGE);
            return false;
        }

        boolean tooLong = messages.stream()
                .anyMatch(m -> "user".equals(m.getRole())
                        && m.getContent() != null
                        && m.getContent().length() > MAX_MESSAGE_CHARACTERS);
        if (tooLong) {
            chatRequest.addMessage("assistant", ASSISTANT_SUPPORT_MESSAGE);
            return false;
        }

        if (messages.size() >= MAX_MESSAGES) {
            chatRequest.addMessage("assistant", ASSISTANT_SUPPORT_MESSAGE);
            return false;
        }

        return true;
    }

    private HttpEntity<Map<String, Object>> buildOpenAIRequest(List<ChatMessageDTO> messages) {
        List<Map<String, String>> messagePayload = messages.stream()
                .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
                .collect(Collectors.toList());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + openaiApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4.1-nano");
        body.put("max_completion_tokens", MAX_TOKEN_USAGE_BY_API);
        body.put("messages", messagePayload);

        return new HttpEntity<>(body, headers);
    }

    private ResponseEntity<String> callOpenAI(HttpEntity<Map<String, Object>> request) {
        return restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                request,
                String.class
        );
    }

    private Optional<String> parseOpenAIResponse(ResponseEntity<String> response) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return Optional.empty();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String reply = root.path("choices").get(0).path("message").path("content").asText(null);
            return Optional.ofNullable(reply).filter(r -> !r.isBlank());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void addAssistantOrFallback(ChatRequest chatRequest, Optional<String> maybeReply) {
        chatRequest.addMessage("assistant", maybeReply.orElse(ASSISTANT_SUPPORT_MESSAGE));
    }

}
