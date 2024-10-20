package com.robobg.entity.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionWithAnswersDTO {
    private Long id;
    private AuthorDTO author;
    private String text;
    private LocalDateTime createTime;
    private List<AnswerDTO> answers;
}
