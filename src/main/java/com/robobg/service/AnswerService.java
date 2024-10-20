package com.robobg.service;

import com.robobg.entity.Answer;
import com.robobg.entity.dtos.AnswerCreateDTO;
import com.robobg.exceptions.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface AnswerService {

    void createAnswer(AnswerCreateDTO answerCreateDTO, HttpServletRequest request) throws EntityNotFoundException;

    void deleteAnswer(Long answerId, HttpServletRequest request);

    Answer findById(Long id) throws EntityNotFoundException;

}
