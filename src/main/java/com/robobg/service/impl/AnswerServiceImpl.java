package com.robobg.service.impl;

import com.robobg.config.JwtService;
import com.robobg.entity.Answer;
import com.robobg.entity.User;
import com.robobg.entity.dtos.AnswerCreateDTO;
import com.robobg.exceptions.EntityNotFoundException;
import com.robobg.repository.AnswerRepository;
import com.robobg.repository.UserRepository;
import com.robobg.service.AnswerService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    @Autowired
    private ModelMapper modelMapper;

    public AnswerServiceImpl(AnswerRepository answerRepository, UserRepository userRepository, JwtService jwtService) {
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }





    @Override
    public void createAnswer(AnswerCreateDTO answerCreateDTO, HttpServletRequest request) {
        String token = extractJwtFromRequest(request);
        String tokenUsername = jwtService.extractUsername(token);
        String requestUsername = answerCreateDTO.getAuthorUsername();
        if (!tokenUsername.equals(requestUsername)) {
            throw new IllegalArgumentException("Something went wrong!");
        }
        Answer answer = modelMapper.map(answerCreateDTO, Answer.class);
        answer.setCreateTime(LocalDateTime.now());
        Optional<User> userOptional = userRepository.findByUsername(answerCreateDTO.getAuthorUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            answer.setAuthor(user);
            answerRepository.save(answer);
        } else {
            throw new IllegalArgumentException("Something went wrong!");
        }

    }

    @Override
    public void deleteAnswer(Long answerId, HttpServletRequest request) {
        String token = extractJwtFromRequest(request);
        String tokenUsername = jwtService.extractUsername(token);
        Optional<Answer> answer = answerRepository.findById(answerId);
        String authorUsername = "";
        if(answer.isPresent()){
            authorUsername = answer.get().getAuthor().getUsername();
        } else {
            throw new IllegalArgumentException("Something went wrong!");
        }
        String tokenRole = jwtService.extractRole(token);
        if("ADMIN".equals(tokenRole)){
            answerRepository.deleteById(answerId);
        } else if (tokenUsername.equals(authorUsername)) {
            answerRepository.deleteById(answerId);
        } else {
            throw new IllegalArgumentException("Something went wrong!");
        }
        answerRepository.deleteById(answerId);
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
