package com.robobg.controller;

import com.robobg.entity.dtos.ConsumableDTO.CreateConsumableDTO;
import com.robobg.entity.dtos.QnaDTO.LatestQuestionsDTO;
import com.robobg.entity.dtos.RobotDTO.CreateMostComparedDTO;
import com.robobg.entity.dtos.RobotDTO.CreatePurchaseLinkDTO;
import com.robobg.entity.dtos.RobotDTO.CreateRobotDTO;
import com.robobg.entity.dtos.UserDTO.UserIdUsernameRoleDTO;
import com.robobg.exceptions.RobotAlreadyExistsException;
import com.robobg.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/moderator")
public class ModeratorController {
    private final RobotService robotService;
    private final UserService userService;
    private final PurchaseLinkService purchaseLinkService;
    private final MostComparedService mostComparedService;
    private final QuestionService questionService;
    private final ConsumableService consumableService;
    @Autowired
    public ModeratorController(RobotService robotService, UserService userService, PurchaseLinkService purchaseLinkService, MostComparedService mostComparedService, QuestionService questionService, ConsumableService consumableService) {
        this.robotService = robotService;
        this.userService = userService;
        this.purchaseLinkService = purchaseLinkService;
        this.mostComparedService = mostComparedService;
        this.questionService = questionService;
        this.consumableService = consumableService;
    }

    @DeleteMapping("/robots/{id}")
    public void deleteRobotById (@PathVariable("id") Long id) throws ChangeSetPersister.NotFoundException {
        robotService.deleteRobotById(id);
    }

    @PostMapping("/robots")
    public void createRobot (@RequestBody CreateRobotDTO createRobotDTO) throws RobotAlreadyExistsException {
        robotService.saveRobot(createRobotDTO);
    }

    @PutMapping("/robots")
    public void updateRobot(@RequestBody CreateRobotDTO robot) {
        robotService.updateRobot(robot);
    }


    @GetMapping("/users")
    public List<UserIdUsernameRoleDTO> getAllUsers() {
        return userService.getAll();
    }


    @PostMapping("/links")
    public void createPurchaseLink(@RequestBody CreatePurchaseLinkDTO createPurchaseLinkDTO){
        purchaseLinkService.createPurchaseLink(createPurchaseLinkDTO);
    }

    @DeleteMapping("/links/{id}")
    public void delete(@PathVariable Long id) {
        purchaseLinkService.deletePurchaseLink(id);
    }

    @PostMapping("/most-compared")
    public void createMostCompared(@RequestBody CreateMostComparedDTO createMostComparedDTO){
        mostComparedService.createMostCompared(createMostComparedDTO);
    }

    @PutMapping("/most-compared")
    public void updateMostCompared(@RequestBody CreateMostComparedDTO updateMostComparedDTO){
        mostComparedService.updateMostCompared(updateMostComparedDTO);
    }

    @DeleteMapping("/most-compared/{id}")
    public void deleteMostCompared(@PathVariable Long id){
        mostComparedService.deleteMostCompared(id);
    }

    @PostMapping("/robots/{robotId}/image")
    public void uploadRobotImage(@PathVariable("robotId") Long robotId,
                                 @RequestParam("file")MultipartFile file) throws IOException {
        robotService.uploadRobotImage(robotId,file);

    }

    @DeleteMapping("/consumable/{id}")
    public void deleteConsumable(@PathVariable Long id) {
        consumableService.deleteConsumable(id);
    }

    @PostMapping("/consumable")
    public void createConsumable(@RequestBody CreateConsumableDTO createConsumableDTO){
        consumableService.createConsumableService(createConsumableDTO);
    }

    @PostMapping("/consumable/{consumableId}/images")
    public void uploadConsumableImages(@PathVariable("consumableId") Long consumableId,
                                       @RequestParam("file") MultipartFile[] files) throws IOException {
        List<MultipartFile> fileList = Arrays.asList(files);  // Convert array to List
        consumableService.uploadConsumableImage(consumableId, fileList);  // Pass the List to the service
    }


    @PutMapping("/consumable")
    public void updateConsumable(@RequestBody CreateConsumableDTO updateConsumableDTO) {
        consumableService.updateConsumable(updateConsumableDTO);
    }

    @GetMapping("/questions")
    public List<LatestQuestionsDTO> latestQuestions(){
        return questionService.getLatestQuestions();
    }


}
