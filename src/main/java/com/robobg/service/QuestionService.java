package com.robobg.service;

import com.robobg.entity.Question;
import com.robobg.entity.dtos.LatestQuestionsDTO;
import com.robobg.entity.dtos.QuestionCreateDTO;
import com.robobg.entity.dtos.QuestionWithAnswersDTO;
import com.robobg.exceptions.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuestionService {
    List<QuestionWithAnswersDTO> findQuestionsByRobotId(Long robotId);
    void createQuestion(QuestionCreateDTO questionDTO, HttpServletRequest request) throws EntityNotFoundException;
    void deleteQuestion(Long questionId, HttpServletRequest request) throws EntityNotFoundException;
    Question findById(Long id) throws EntityNotFoundException;

    List<LatestQuestionsDTO> getLatestQuestions();
}
