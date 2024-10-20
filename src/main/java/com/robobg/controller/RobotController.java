package com.robobg.controller;

import com.robobg.entity.dtos.QuestionWithAnswersDTO;
import com.robobg.entity.dtos.RobotDTO.RobotDTO;
import com.robobg.entity.dtos.RobotDTO.RobotModelImageLinksDTO;
import com.robobg.entity.dtos.RobotDTO.RobotResponse;
import com.robobg.service.QuestionService;
import com.robobg.service.RobotService;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
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

    @GetMapping
    public RobotResponse getRobots(@RequestParam(required = false) HashSet<String> fields,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "") String model,
                                   @RequestParam(defaultValue = "") List<String> brands,
                                   @RequestParam(defaultValue = "0") Integer startYear,
                                   @RequestParam(defaultValue = "0") Integer endYear,
                                   @RequestParam(defaultValue = "0") Integer minDustbinCapacity,
                                   @RequestParam(defaultValue = "0") Integer maxDustbinCapacity,
                                   @RequestParam(defaultValue = "0") Integer minSuctionPower,
                                   @RequestParam(defaultValue = "0") Integer maxSuctionPower
    ) {
        return robotService.getRobots(fields,page,model,brands,startYear,endYear,minDustbinCapacity,maxDustbinCapacity,minSuctionPower,maxSuctionPower);
    }

    @GetMapping("/bests")
    public List<RobotModelImageLinksDTO> getBestRobots(){
        return robotService.findAllBests();
    }


    @GetMapping("/{robotId}/questions")
    public List<QuestionWithAnswersDTO> getAllQuestionsByRobotId(@PathVariable Long robotId) {
        return questionService.findQuestionsByRobotId(robotId).stream().toList();
    }


}
