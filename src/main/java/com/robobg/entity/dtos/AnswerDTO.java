package com.robobg.entity.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerDTO {
    private Long id;
    private AuthorDTO author;
    private String text;
    private LocalDateTime createTime;
}
