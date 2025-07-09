package com.robobg.dtos.openai;


import lombok.Data;

@Data
public class ChatMessageDTO {
    private String role;
    private String content;

    public ChatMessageDTO(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
