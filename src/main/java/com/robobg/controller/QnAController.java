package com.robobg.controller;


import com.robobg.dtos.QnaDTO.QuestionCreateDTO;
import com.robobg.exceptions.EntityNotFoundException;
import com.robobg.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/qna")
public class QnAController {
    private final QuestionService questionService;

    public QnAController(QuestionService questionService) {
        this.questionService = questionService;
    }


    @PostMapping("/questions")
    public ResponseEntity<String> createQuestion(@Valid @RequestBody QuestionCreateDTO questionCreateDTO, HttpServletRequest request) throws EntityNotFoundException {
        questionService.createQuestion(questionCreateDTO,request);
        return ResponseEntity.ok().build();
    }
}
