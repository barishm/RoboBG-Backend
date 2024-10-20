package com.robobg.controller;

import com.robobg.config.JwtService;
import com.robobg.entity.dtos.AnswerCreateDTO;
import com.robobg.entity.dtos.QuestionCreateDTO;
import com.robobg.exceptions.EntityNotFoundException;
import com.robobg.service.AnswerService;
import com.robobg.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/v1/user")
public class UserController {
    private final QuestionService questionService;
    private final AnswerService answerService;

    public UserController(QuestionService questionService, AnswerService answerService, JwtService jwtService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }

    @PostMapping("/answers")
    public ResponseEntity<String> createAnswer(@Valid @RequestBody AnswerCreateDTO answerCreateDTO, HttpServletRequest request) throws EntityNotFoundException {
        answerService.createAnswer(answerCreateDTO,request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/answers/{id}")
    public ResponseEntity<String> deleteAnswer(@PathVariable Long id, HttpServletRequest request) throws EntityNotFoundException {
        answerService.deleteAnswer(id,request);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id, HttpServletRequest request) throws EntityNotFoundException {
        questionService.deleteQuestion(id,request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions")
    public ResponseEntity<String> createQuestion(@Valid @RequestBody QuestionCreateDTO questionCreateDTO, HttpServletRequest request) throws EntityNotFoundException {
        questionService.createQuestion(questionCreateDTO,request);
        return ResponseEntity.ok().build();
    }

}
