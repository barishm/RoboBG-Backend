package com.robobg.service.impl;

import com.robobg.dtos.RobotDTO.*;
import com.robobg.entity.PurchaseLink;
import com.robobg.entity.Robot;
import com.robobg.exceptions.RobotAlreadyExistsException;
import com.robobg.repository.QuestionRepository;
import com.robobg.repository.RobotRepository;
import com.robobg.service.MostComparedService;
import com.robobg.service.RobotService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RobotServiceImpl implements RobotService {


    private final RobotRepository robotRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;
    private final MostComparedService mostComparedService;
    private final AvailableBrandsServiceImpl availableBrandsService;
    private final ImageService imageService;
    @Value("${app.files.path}")
    private String appFilesPath;
    @Value("${domain}")
    private String domain;
    @Value("${web.protocol}")
    private String webProtocol;


    @Autowired
    public RobotServiceImpl(RobotRepository robotRepository,
                            ModelMapper modelMapper,
                            MostComparedService mostComparedService,
                            AvailableBrandsServiceImpl availableBrandsService,
                            ImageService imageService,
                            QuestionRepository questionRepository) {
        super();
        this.robotRepository = robotRepository;
        this.modelMapper = modelMapper;
        this.mostComparedService = mostComparedService;
        this.availableBrandsService = availableBrandsService;
        this.imageService = imageService;
        this.questionRepository = questionRepository;
    }


    private String buildFullImageUrl(String image) {
        if (image == null || image.startsWith("http")) {
            return image;
        }
        return webProtocol+"://api."+domain+"/files" + "/" + image;

    }




    @Override
    public void saveRobot(CreateRobotDTO dto) throws RobotAlreadyExistsException {

        if (robotRepository.existsByModel(dto.getModel())) {
            throw new RobotAlreadyExistsException("Robot already exists");
        }

        availableBrandsService.increaseCount(dto.getBrand());

        Robot robot = modelMapper.map(dto, Robot.class);

        if (dto.getPurchaseLinks() != null) {
            for (PurchaseLinkDTO linkDTO : dto.getPurchaseLinks()) {
                PurchaseLink link = modelMapper.map(linkDTO, PurchaseLink.class);
                robot.addPurchaseLink(link);
            }
        }

        robotRepository.save(robot);
    }

    @Override
    @Transactional
    public void updateRobot(CreateRobotDTO dto) {

        Robot robot = robotRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Robot not found"));

        String image = robot.getImage();

        modelMapper.map(dto, robot);
        robot.setImage(image);

        if (dto.getPurchaseLinks() != null) {

            robot.clearPurchaseLinks();

            for (PurchaseLinkDTO linkDTO : dto.getPurchaseLinks()) {
                PurchaseLink link = modelMapper.map(linkDTO, PurchaseLink.class);
                robot.addPurchaseLink(link);
            }
        }
    }

    @Override
    public void deleteRobotById(Long id) throws NotFoundException {
        Optional<Robot> optionalRobot = robotRepository.findById(id);
        if (optionalRobot.isPresent()) {
            Robot robot = optionalRobot.get();
            String imageFileName = robot.getImage();

            // Delete image from local file system
            if (imageFileName != null) {
                Path imagePath = Paths.get(appFilesPath, imageFileName);
                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    // Log and proceed — deletion failure shouldn't block overall delete
                    System.err.println("Failed to delete image file: " + imagePath);
                    e.printStackTrace();
                }
            }
            mostComparedService.deleteMostComparedEntityIfRobotWithIdExist(id);
            robotRepository.delete(robot);
        } else {
            throw new NotFoundException();
        }
    }


    @Override
    public void uploadRobotImage(Long robotId, MultipartFile file) throws IOException {

        System.out.println("Starting image upload for robot ID: " + robotId);

        Robot robot = robotRepository.findById(robotId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Robot with ID " + robotId + " does not exist."
                ));

        // Delete old image if exists
        if (robot.getImage() != null) {
            Path oldImagePath = Paths.get("app/files", robot.getImage());
            Files.deleteIfExists(oldImagePath);
        }

        // Delegate ALL image processing to ImageService
        String newFileName = imageService.storeRobotImage(robotId, file);

        robot.setImage(newFileName);
        robotRepository.save(robot);

        System.out.println("Image uploaded successfully for robot ID: " + robotId);
    }


    @Override
    public Optional<RobotDTO> getRobotById(Long id) {
        return robotRepository.findById(id)
                .map(robot -> {
                    RobotDTO dto = modelMapper.map(robot, RobotDTO.class);
                    dto.setImage(buildFullImageUrl(dto.getImage()));
                    return dto;
                });
    }

    @Override
    public List<RobotsListDTO> getAllRobots() {
        List<Robot> allRobots = robotRepository.findAll();

        return allRobots.stream()
                .map(robot -> {
                    RobotsListDTO dto = modelMapper.map(robot, RobotsListDTO.class);
                    dto.setImage(buildFullImageUrl(dto.getImage()));

                    long count = questionRepository.countByRobotId(robot.getId());
                    dto.setQnaCount((int) count);

                    return dto;
                })
                .toList();
    }


}
