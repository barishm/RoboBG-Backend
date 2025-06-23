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
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RobotServiceImpl implements RobotService {


    private final RobotRepository robotRepository;
    private final ModelMapper modelMapper;
    private final MostComparedService mostComparedService;
    private final AvailableBrandsServiceImpl availableBrandsService;
    private static final String SAVE_IMAGE_PATH = "/home/ubuntu/robobg/images";
    private static final String IMAGE_BASE_URL = "https://api.barishm.com/images/";

    @Autowired
    public RobotServiceImpl(RobotRepository robotRepository, ModelMapper modelMapper, MostComparedService mostComparedService, AvailableBrandsServiceImpl availableBrandsService) {
        super();
        this.robotRepository = robotRepository;
        this.modelMapper = modelMapper;
        this.mostComparedService = mostComparedService;
        this.availableBrandsService = availableBrandsService;
    }

    @Override
    public List<RobotsListDTO> findAllBests() {
        return robotRepository.findAllBests().stream()
                .filter(robot -> Boolean.TRUE.equals(robot.getBests()))
                .limit(9)
                .map(robot -> {
                    RobotsListDTO dto = modelMapper.map(robot, RobotsListDTO.class);
                    if (dto.getImage() != null && !dto.getImage().startsWith("http")) {
                        dto.setImage(IMAGE_BASE_URL + dto.getImage());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RobotResponse getAllModels() {
        RobotResponse robotResponse = new RobotResponse();
        robotResponse.setContent(robotRepository.findAll().stream()
                .map(robot -> modelMapper.map(robot, RobotModelDTO.class))
                .collect(Collectors.toList()));
        return robotResponse;
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
                Path imagePath = Paths.get(SAVE_IMAGE_PATH, imageFileName);
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

        Optional<Robot> robotOptional = robotRepository.findById(robotId);
        if (robotOptional.isEmpty()) {
            System.out.println("Robot not found: " + robotId);
            throw new IllegalArgumentException("Robot with ID " + robotId + " does not exist.");
        }

        Robot robot = robotOptional.get();

        // Delete old image if exists
        if (robot.getImage() != null) {
            Path oldImagePath = Paths.get(SAVE_IMAGE_PATH, robot.getImage());
            System.out.println("Deleting old image: " + oldImagePath);
            Files.deleteIfExists(oldImagePath);
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);

        String extension = getExtensionOfFile(file);
        System.out.println("File extension: " + extension);
        if (extension.isEmpty()) {
            System.out.println("Unsupported file type.");
            throw new IllegalArgumentException("Unsupported file type.");
        }

        String fileName = "Robot%s_%s.%s".formatted(robotId, timestamp, extension);
        Path imagePath = Paths.get(SAVE_IMAGE_PATH, fileName);

        System.out.println("Saving image to: " + imagePath);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, file.getBytes());

        robot.setImage(fileName);
        robotRepository.save(robot);

        System.out.println("Image uploaded and saved successfully for robot ID: " + robotId);
    }



    private String getExtensionOfFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    private String determineContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case "png" -> "image/png";
            case "jpg" -> "image/jpg";
            case "jpeg" -> "image/jpeg";
            case "avif" -> "image/avif";
            case "webp" -> "image/webp";
            default -> ""; // Unsupported type
        };
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
                    if (dto.getImage() != null && !dto.getImage().startsWith("http")) {
                        dto.setImage(IMAGE_BASE_URL + dto.getImage());
                    }
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
                    if (dto.getImage() != null && !dto.getImage().startsWith("http")) {
                        dto.setImage(IMAGE_BASE_URL + dto.getImage());
                    }
                    return dto;
                })
                .toList();
    }


}
