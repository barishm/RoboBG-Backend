package com.robobg.dtos.openai;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatRequest {
    private String sessionId;
    private List<ChatMessageDTO> messages = new ArrayList<>();

    public void addMessage(String role, String content) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(new ChatMessageDTO(role, content));
    }
}
