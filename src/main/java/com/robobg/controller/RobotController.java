package com.robobg.controller;

import com.robobg.dtos.QnaDTO.QuestionWithAnswersDTO;
import com.robobg.dtos.RobotDTO.RobotDTO;
import com.robobg.dtos.RobotDTO.RobotsListDTO;
import com.robobg.service.QuestionService;
import com.robobg.service.RobotService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/robots")
public class RobotController {
    private final RobotService robotService;
    private final QuestionService questionService;

    public RobotController(RobotService robotService, QuestionService questionService) {
        this.robotService = robotService;
        this.questionService = questionService;
    }

    @GetMapping("/{id}")
    public Optional<RobotDTO> getRobotById(@PathVariable("id") Long id) {
        return robotService.getRobotById(id);
    }


    @GetMapping("/all")
    public List<RobotsListDTO> getAllRobots() {
        return robotService.getAllRobots();
    }



    @GetMapping("/{robotId}/questions")
    public List<QuestionWithAnswersDTO> getAllQuestionsByRobotId(@PathVariable Long robotId) {
        return questionService.findQuestionsByRobotId(robotId).stream().toList();
    }


}
