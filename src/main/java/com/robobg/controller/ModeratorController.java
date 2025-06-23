package com.robobg.controller;

import com.robobg.dtos.ConsumableDTO.CreateConsumableDTO;
import com.robobg.dtos.QnaDTO.LatestQuestionsDTO;
import com.robobg.dtos.RobotDTO.CreateMostComparedDTO;
import com.robobg.dtos.RobotDTO.CreatePurchaseLinkDTO;
import com.robobg.dtos.RobotDTO.CreateRobotDTO;
import com.robobg.dtos.UserDTO.UserIdUsernameRoleDTO;
import com.robobg.exceptions.EntityNotFoundException;
import com.robobg.exceptions.RobotAlreadyExistsException;
import com.robobg.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ModeratorController.class);
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
        logger.info("Received upload request for robotId: {}", robotId);
        logger.info("File original name: {}", file.getOriginalFilename());
        logger.info("File size: {} bytes", file.getSize());
        robotService.uploadRobotImage(robotId,file);

    }

    @DeleteMapping("/consumable/{id}")
    public void deleteConsumable(@PathVariable Long id) throws EntityNotFoundException {
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
