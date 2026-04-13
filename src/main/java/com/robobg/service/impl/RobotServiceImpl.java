package com.robobg.service.impl;

import com.robobg.dtos.RobotDTO.CreateRobotDTO;
import com.robobg.dtos.RobotDTO.RobotDTO;
import com.robobg.dtos.RobotDTO.RobotModelDTO;
import com.robobg.dtos.RobotDTO.RobotsListDTO;
import com.robobg.entity.Robot;
import com.robobg.entity.oldDtos.RobotModelImageLinksDTO;
import com.robobg.entity.oldDtos.RobotResponse;
import com.robobg.exceptions.RobotAlreadyExistsException;
import com.robobg.repository.RobotRepository;
import com.robobg.repository.RobotSpecifications;
import com.robobg.service.MostComparedService;
import com.robobg.service.RobotService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
                            ImageService imageService) {
        super();
        this.robotRepository = robotRepository;
        this.modelMapper = modelMapper;
        this.mostComparedService = mostComparedService;
        this.availableBrandsService = availableBrandsService;
        this.imageService = imageService;
    }


    @Override
    public RobotResponse getAllModels() {
        RobotResponse robotResponse = new RobotResponse();
        robotResponse.setContent(robotRepository.findAll().stream()
                .map(robot -> modelMapper.map(robot, RobotModelDTO.class))
                .collect(Collectors.toList()));
        return robotResponse;
    }

    private String buildFullImageUrl(String image) {
        if (image == null || image.startsWith("http")) {
            return image;
        }
        return webProtocol+"://api."+domain+"/files" + "/" + image;

    }




    @Override
    public RobotResponse getAllRobotIdModelImageLinks(int page, String model, List<String> brands, Integer startYear, Integer endYear, Integer minDustbinCapacity, Integer maxDustbinCapacity, Integer minSuctionPower, Integer maxSuctionPower) {
        Pageable pageable = PageRequest.of(page,12);
        Page<Robot> robots;
        Specification<Robot> spec = Specification.where(null);
        if (minSuctionPower != 0 && maxSuctionPower != 0) {
            spec = spec.and(RobotSpecifications.hasSuctionPowerBetween(minSuctionPower, maxSuctionPower));
        }
        if (minDustbinCapacity != 0 && maxDustbinCapacity != 0) {
            spec = spec.and(RobotSpecifications.hasDustbinCapacityBetween(minDustbinCapacity, maxDustbinCapacity));
        }
        if (startYear != 0 && endYear != 0) {
            spec = spec.and(RobotSpecifications.hasReleaseYearBetween(startYear, endYear));
        }
        if (model != null && !model.isEmpty()) {
            spec = spec.and(RobotSpecifications.modelContains(model));
        }
        if (brands != null && !brands.isEmpty()) {
            spec = spec.and(RobotSpecifications.brandIn(brands));
        }
        robots = robotRepository.findAll(spec, pageable);
        List<Robot> listOfRobots = robots.getContent();
        List<RobotModelImageLinksDTO> content = listOfRobots.stream().map(robot -> modelMapper.map(robot, RobotModelImageLinksDTO.class)).collect(Collectors.toList());
        RobotResponse robotResponse = new RobotResponse();
        robotResponse.setContent(content);
        robotResponse.setPageNo(robots.getNumber());
        robotResponse.setTotalPages(robots.getTotalPages());
        robotResponse.setLast(robots.isLast());
        return robotResponse;
    }

    @Override
    public void saveRobot(CreateRobotDTO createRobotDTO) throws RobotAlreadyExistsException {
        if (robotRepository.existsByModel(createRobotDTO.getModel())) {
            throw new RobotAlreadyExistsException("Robot already exists");
        }

        availableBrandsService.increaseCount(createRobotDTO.getBrand());
        Robot robot = modelMapper.map(createRobotDTO, Robot.class);
        robotRepository.save(robot);
    }

    @Override
    public void updateRobot(CreateRobotDTO createRobotDTO) {
        Robot robot = modelMapper.map(createRobotDTO, Robot.class);
        String image = robotRepository.findImageById(createRobotDTO.getId());
        robot.setImage(image);
        robotRepository.save(robot);
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
            availableBrandsService.decreaseCount(robot.getBrand());
            mostComparedService.deleteMostComparedEntityIfRobotWithIdExist(id);
            robotRepository.deleteById(id);
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
    public RobotResponse getRobots(HashSet<String> fields, int page, String model, List<String> brands,Integer startYear,Integer endYear,Integer minDustbinCapacity,Integer maxDustbinCapacity,Integer minSuctionPower,Integer maxSuctionPower) {
        if (fields.containsAll(Arrays.asList("model", "image", "links"))) {
            return getAllRobotIdModelImageLinks(page,model,brands,startYear,endYear,minDustbinCapacity,maxDustbinCapacity,minSuctionPower,maxSuctionPower);
        }else if (fields.contains("model")) {
            return getAllModels();
        }
        return null;
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

    public void incrementQnaCount(RobotDTO robotDTO) {
        Integer currentQnaCount = robotDTO.getQnaCount();
        if (currentQnaCount == null) {
            currentQnaCount = 0;
        }
        robotDTO.setQnaCount(currentQnaCount + 1);
        Robot robot = modelMapper.map(robotDTO, Robot.class);
        robotRepository.save(robot);
    }

    @Override
    public List<RobotsListDTO> getAllRobots() {
        List<Robot> allRobots = robotRepository.findAll();
        return allRobots.stream()
                .map(robot -> {
                    RobotsListDTO dto = modelMapper.map(robot, RobotsListDTO.class);
                    dto.setImage(buildFullImageUrl(dto.getImage()));
                    return dto;
                })
                .toList();
    }


}
