package com.robobg.service.impl;

import com.robobg.config.JwtService;
import com.robobg.entity.Answer;
import com.robobg.entity.Question;
import com.robobg.entity.User;
import com.robobg.dtos.QnaDTO.AnswerCreateDTO;
import com.robobg.exceptions.EntityNotFoundException;
import com.robobg.repository.AnswerRepository;
import com.robobg.repository.QuestionRepository;
import com.robobg.repository.UserRepository;
import com.robobg.service.AnswerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final QuestionRepository questionRepository;
    @Autowired
    private ModelMapper modelMapper;

    public AnswerServiceImpl(AnswerRepository answerRepository, UserRepository userRepository, JwtService jwtService,QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.questionRepository = questionRepository;
    }





    @Override
    @Transactional
    public void createAnswer(AnswerCreateDTO dto, HttpServletRequest request) {

        // 🔐 MUST be authenticated
        String token = extractJwtFromRequest(request);

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Authentication required");
        }

        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        Answer answer = new Answer();
        answer.setText(dto.getText());
        answer.setCreateTime(OffsetDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        answer.setAuthor(user);
        answer.setQuestion(question);

        answer.setAvatar(dto.getAvatar());

        answerRepository.save(answer);
    }

    @Override
    @Transactional
    public void deleteAnswer(Long answerId, HttpServletRequest request) {

        String token = extractJwtFromRequest(request);
        String tokenUsername = jwtService.extractUsername(token);
        String tokenRole = jwtService.extractRole(token);

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found"));

        String authorUsername = answer.getAuthor().getUsername();

        boolean isAdmin = "ADMIN".equals(tokenRole);
        boolean isOwner = tokenUsername.equals(authorUsername);

        if (isAdmin || isOwner) {
            answerRepository.delete(answer);
        } else {
            throw new IllegalArgumentException("You are not allowed to delete this answer");
        }
    }

    @Override
    public Answer findById(Long id) throws EntityNotFoundException {
        Optional<Answer> result = answerRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new EntityNotFoundException("Answer not found with id: " + id);
        }
    }
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
