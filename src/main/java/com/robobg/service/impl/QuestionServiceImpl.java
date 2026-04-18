package com.robobg.service.impl;

import com.robobg.config.JwtService;
import com.robobg.dtos.QnaDTO.*;
import com.robobg.entity.Answer;
import com.robobg.entity.Question;
import com.robobg.entity.Robot;
import com.robobg.entity.User;
import com.robobg.exceptions.EntityNotFoundException;
import com.robobg.repository.QuestionRepository;
import com.robobg.repository.RobotRepository;
import com.robobg.repository.UserRepository;
import com.robobg.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RobotRepository robotRepository;
    @Autowired
    private ModelMapper modelMapper;

    public QuestionServiceImpl(QuestionRepository questionRepository, UserRepository userRepository, JwtService jwtService, RobotRepository robotRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.robotRepository = robotRepository;
    }



    @Override
    public List<QuestionWithAnswersDTO> findQuestionsByRobotId(Long robotId) {

        List<Question> questions = questionRepository.findByRobotId(robotId);

        return questions.stream().map(question -> {

            QuestionWithAnswersDTO dto = new QuestionWithAnswersDTO();

            dto.setId(question.getId());
            dto.setText(question.getText());
            dto.setCreateTime(question.getCreateTime());

            AuthorDTO authorDTO = new AuthorDTO();

            if (question.getAuthor() != null) {
                authorDTO.setName(question.getAuthor().getUsername());
                authorDTO.setAvatar(question.getAvatar());
                authorDTO.setAnonymous(false);
            } else {
                authorDTO.setName(question.getAnonymousName());
                authorDTO.setAvatar(question.getAvatar());
                authorDTO.setAnonymous(true);
            }

            dto.setAuthor(authorDTO);

            List<AnswerDTO> answerDTOs = question.getAnswers().stream()
                    .map(answer -> {
                        AnswerDTO a = new AnswerDTO();
                        a.setId(answer.getId());
                        a.setText(answer.getText());
                        a.setCreateTime(answer.getCreateTime());

                        AuthorDTO answerAuthor = new AuthorDTO();
                        answerAuthor.setName(answer.getAuthor().getUsername());
                        answerAuthor.setAvatar(answer.getAvatar());
                        answerAuthor.setAnonymous(false);

                        a.setAuthor(answerAuthor);

                        return a;
                    })
                    .toList();

            dto.setAnswers(answerDTOs);

            return dto;

        }).toList();
    }



    @Override
    @Transactional
    public void createQuestion(QuestionCreateDTO dto, HttpServletRequest request) {

        User user = null;

        String token = extractJwtFromRequest(request);

        if (token != null && !token.isBlank()) {
            String username = jwtService.extractUsername(token);

            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }

        if (user == null) {

            String name = dto.getAnonymousName();

            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Anonymous name is required for unauthenticated users");
            }

            if (name.length() < 3 || name.length() > 12) {
                throw new IllegalArgumentException("Anonymous name must be between 3 and 12 characters");
            }
        }

        Robot robot = robotRepository.findById(dto.getRobotId())
                .orElseThrow(() -> new IllegalArgumentException("Robot not found"));

        Question question = new Question();
        question.setText(dto.getText());
        question.setAvatar(dto.getAvatar());
        question.setCreateTime(OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        question.setRobot(robot);

        if (user != null) {
            question.setAuthor(user);
        } else {
            question.setAnonymousName(dto.getAnonymousName());
        }

        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId, HttpServletRequest request) throws EntityNotFoundException {

        String token = extractJwtFromRequest(request);
        String tokenUsername = jwtService.extractUsername(token);
        String tokenRole = jwtService.extractRole(token);

        Question question = findById(questionId);

        boolean isAdmin = "ADMIN".equals(tokenRole);

        boolean isOwner = question.getAuthor() != null &&
                tokenUsername.equals(question.getAuthor().getUsername());

        if (isAdmin || isOwner) {
            questionRepository.delete(question);
        } else {
            throw new IllegalArgumentException("You are not allowed to delete this question");
        }
    }


    @Override
    public Question findById(Long id) throws EntityNotFoundException {
        Optional<Question> result = questionRepository.findById(id);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new EntityNotFoundException("Question not found with id: " + id);
        }
    }

    @Override
    public List<LatestQuestionsDTO> getLatestQuestions() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Question> questionsLast7Days = questionRepository.findByCreateTimeAfter(sevenDaysAgo);
        questionsLast7Days.sort(Comparator.comparing(Question::getCreateTime).reversed());
        return questionsLast7Days.stream().map(question -> modelMapper.map(question, LatestQuestionsDTO.class)).collect(Collectors.toList());
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
