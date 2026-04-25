package com.robobg.service.impl;

import com.robobg.dtos.RobotDTO.*;
import com.robobg.entity.*;
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
import java.util.*;
import java.util.List;

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

        Robot robot = new Robot();

        mapBasicFields(dto, robot);

        robot.setCleaningFeatures(mapCleaning(dto.getCleaningFeatures()));
        robot.setMoppingFeatures(mapMopping(dto.getMoppingFeatures()));
        robot.setBattery(mapBattery(dto.getBattery()));
        robot.setControl(mapControl(dto.getControl()));
        robot.setAppFeatures(mapAppFeatures(dto.getAppFeatures()));
        robot.setSensor(mapSensor(dto.getSensor()));
        robot.setOtherSpecifications(mapOther(dto.getOtherSpecifications()));

        if (dto.getPurchaseLinks() != null) {
            for (PurchaseLinkDTO linkDTO : dto.getPurchaseLinks()) {
                PurchaseLink link = new PurchaseLink();
                link.setName(linkDTO.getName());
                link.setLink(linkDTO.getLink());
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

        // basic fields only
        mapBasicFields(dto, robot);
        robot.setImage(image);

        // OneToOne updates (NEVER replace IDs or objects)
        updateCleaning(dto.getCleaningFeatures(), robot);
        updateMopping(dto.getMoppingFeatures(), robot);
        updateBattery(dto.getBattery(), robot);
        updateControl(dto.getControl(), robot);
        updateAppFeatures(dto.getAppFeatures(), robot);
        updateSensor(dto.getSensor(), robot);
        updateOther(dto.getOtherSpecifications(), robot);

        // OneToMany
        if (dto.getPurchaseLinks() != null) {
            robot.clearPurchaseLinks();

            for (PurchaseLinkDTO linkDTO : dto.getPurchaseLinks()) {
                PurchaseLink link = new PurchaseLink();
                link.setName(linkDTO.getName());
                link.setLink(linkDTO.getLink());
                robot.addPurchaseLink(link);
            }
        }
    }

    private void mapBasicFields(CreateRobotDTO dto, Robot robot) {
        robot.setBrand(dto.getBrand());
        robot.setModel(dto.getModel());
        robot.setBests(dto.getBests());
        robot.setMapping(dto.getMapping());
        robot.setMappingSensorType(dto.getMappingSensorType());
        robot.setHighPrecisionMap(dto.getHighPrecisionMap());
        robot.setFrontCamera(dto.getFrontCamera());
        robot.setRechargeResume(dto.getRechargeResume());
        robot.setAutoDockAndRecharge(dto.getAutoDockAndRecharge());
        robot.setNoiseLevel(dto.getNoiseLevel());
        robot.setDisplay(dto.getDisplay());
        robot.setSideBrushes(dto.getSideBrushes());
        robot.setVoicePrompts(dto.getVoicePrompts());
    }
    private AppFeatures mapAppFeatures(AppFeaturesDTO dto) {
        if (dto == null) return null;

        AppFeatures e = new AppFeatures();
        e.setRealTimeTracking(dto.getRealTimeTracking());
        e.setDigitalBlockedAreas(dto.getDigitalBlockedAreas());
        e.setZonedCleaning(dto.getZonedCleaning());
        e.setMultiFloorMaps(dto.getMultiFloorMaps());
        e.setManualMovementControl(dto.getManualMovementControl());
        e.setSelectedRoomCleaning(dto.getSelectedRoomCleaning());
        e.setNoMopZones(dto.getNoMopZones());
        return e;
    }
    private CleaningFeatures mapCleaning(CleaningFeaturesDTO dto) {
        if (dto == null) return null;

        CleaningFeatures e = new CleaningFeatures();
        e.setSuctionPower(dto.getSuctionPower());
        e.setCleaningArea(dto.getCleaningArea());
        e.setDustbinCapacity(dto.getDustbinCapacity());
        e.setDisposableDustBagCapacity(dto.getDisposableDustBagCapacity());
        e.setAutoDirtDisposal(dto.getAutoDirtDisposal());
        e.setBarrierCrossHeight(dto.getBarrierCrossHeight());
        e.setHepaFilter(dto.getHepaFilter());
        e.setWashableFilter(dto.getWashableFilter());
        return e;
    }
    private MoppingFeatures mapMopping(MoppingFeaturesDTO dto) {
        if (dto == null) return null;

        MoppingFeatures e = new MoppingFeatures();
        e.setWetMopping(dto.getWetMopping());
        e.setElectricWaterFlowControl(dto.getElectricWaterFlowControl());
        e.setWaterTankCapacity(dto.getWaterTankCapacity());
        e.setVibratingMoppingPad(dto.getVibratingMoppingPad());
        e.setAutoMopLifting(dto.getAutoMopLifting());
        e.setAutoWaterTankRefilling(dto.getAutoWaterTankRefilling());
        e.setAutoMopWashing(dto.getAutoMopWashing());
        e.setSpinningMops(dto.getSpinningMops());
        e.setWashingMopsWithWarmWater(dto.getWashingMopsWithWarmWater());
        e.setDryingTheMops(dto.getDryingTheMops());
        e.setAutomaticDetergentDosing(dto.getAutomaticDetergentDosing());
        return e;
    }
    private Battery mapBattery(BatteryDTO dto) {
        if (dto == null) return null;

        Battery e = new Battery();
        e.setBatteryCapacity(dto.getBatteryCapacity());
        e.setBatteryLife(dto.getBatteryLife());
        e.setChargingTime(dto.getChargingTime());
        e.setRatedPower(dto.getRatedPower());
        return e;
    }
    private Control mapControl(ControlDTO dto) {
        if (dto == null) return null;

        Control e = new Control();
        e.setScheduling(dto.getScheduling());
        e.setIr_Rf_RemoteControl(dto.getIrRfRemoteControl());
        e.setWifiSmartphoneApp(dto.getWifiSmartphoneApp());
        e.setWifiFrequencyBand(dto.getWifiFrequencyBand());
        e.setAmazonAlexaSupport(dto.getAmazonAlexaSupport());
        e.setGoogleAssistantSupport(dto.getGoogleAssistantSupport());
        e.setMagneticVirtualWalls(dto.getMagneticVirtualWalls());
        return e;
    }
    private Sensor mapSensor(SensorDTO dto) {
        if (dto == null) return null;

        Sensor e = new Sensor();
        e.setCarpetBoost(dto.getCarpetBoost());
        e.setCliffSensor(dto.getCliffSensor());
        e.setDirtSensor(dto.getDirtSensor());
        e.setFullDustbinSensor(dto.getFullDustbinSensor());
        return e;
    }
    private OtherSpecifications mapOther(OtherSpecificationsDTO dto) {
        if (dto == null) return null;

        OtherSpecifications e = new OtherSpecifications();
        e.setWeight(dto.getWeight());
        e.setWidth(dto.getWidth());
        e.setHeight(dto.getHeight());
        e.setInTheBox(dto.getInTheBox());
        e.setReleaseDate(dto.getReleaseDate());
        return e;
    }
    private void updateCleaning(CleaningFeaturesDTO dto, Robot robot) {
        if (dto == null) {
            robot.setCleaningFeatures(null);
            return;
        }

        CleaningFeatures entity = robot.getCleaningFeatures();

        if (entity == null) {
            entity = new CleaningFeatures();
            robot.setCleaningFeatures(entity);
        }

        entity.setSuctionPower(dto.getSuctionPower());
        entity.setCleaningArea(dto.getCleaningArea());
        entity.setDustbinCapacity(dto.getDustbinCapacity());
        entity.setDisposableDustBagCapacity(dto.getDisposableDustBagCapacity());
        entity.setAutoDirtDisposal(dto.getAutoDirtDisposal());
        entity.setBarrierCrossHeight(dto.getBarrierCrossHeight());
        entity.setHepaFilter(dto.getHepaFilter());
        entity.setWashableFilter(dto.getWashableFilter());
    }
    private void updateMopping(MoppingFeaturesDTO dto, Robot robot) {
        if (dto == null) {
            robot.setMoppingFeatures(null);
            return;
        }

        MoppingFeatures entity = robot.getMoppingFeatures();

        if (entity == null) {
            entity = new MoppingFeatures();
            robot.setMoppingFeatures(entity);
        }

        entity.setWetMopping(dto.getWetMopping());
        entity.setElectricWaterFlowControl(dto.getElectricWaterFlowControl());
        entity.setWaterTankCapacity(dto.getWaterTankCapacity());
        entity.setVibratingMoppingPad(dto.getVibratingMoppingPad());
        entity.setAutoMopLifting(dto.getAutoMopLifting());
        entity.setAutoWaterTankRefilling(dto.getAutoWaterTankRefilling());
        entity.setAutoMopWashing(dto.getAutoMopWashing());
        entity.setSpinningMops(dto.getSpinningMops());
        entity.setWashingMopsWithWarmWater(dto.getWashingMopsWithWarmWater());
        entity.setDryingTheMops(dto.getDryingTheMops());
        entity.setAutomaticDetergentDosing(dto.getAutomaticDetergentDosing());
    }
    private void updateBattery(BatteryDTO dto, Robot robot) {
        if (dto == null) {
            robot.setBattery(null);
            return;
        }

        Battery entity = robot.getBattery();

        if (entity == null) {
            entity = new Battery();
            robot.setBattery(entity);
        }

        entity.setBatteryCapacity(dto.getBatteryCapacity());
        entity.setBatteryLife(dto.getBatteryLife());
        entity.setChargingTime(dto.getChargingTime());
        entity.setRatedPower(dto.getRatedPower());
    }
    private void updateControl(ControlDTO dto, Robot robot) {
        if (dto == null) {
            robot.setControl(null);
            return;
        }

        Control entity = robot.getControl();

        if (entity == null) {
            entity = new Control();
            robot.setControl(entity);
        }

        entity.setScheduling(dto.getScheduling());
        entity.setIr_Rf_RemoteControl(dto.getIrRfRemoteControl());
        entity.setWifiSmartphoneApp(dto.getWifiSmartphoneApp());
        entity.setWifiFrequencyBand(dto.getWifiFrequencyBand());
        entity.setAmazonAlexaSupport(dto.getAmazonAlexaSupport());
        entity.setGoogleAssistantSupport(dto.getGoogleAssistantSupport());
        entity.setMagneticVirtualWalls(dto.getMagneticVirtualWalls());
    }
    private void updateAppFeatures(AppFeaturesDTO dto, Robot robot) {
        if (dto == null) {
            robot.setAppFeatures(null);
            return;
        }

        AppFeatures entity = robot.getAppFeatures();

        if (entity == null) {
            entity = new AppFeatures();
            robot.setAppFeatures(entity);
        }

        entity.setRealTimeTracking(dto.getRealTimeTracking());
        entity.setDigitalBlockedAreas(dto.getDigitalBlockedAreas());
        entity.setZonedCleaning(dto.getZonedCleaning());
        entity.setMultiFloorMaps(dto.getMultiFloorMaps());
        entity.setManualMovementControl(dto.getManualMovementControl());
        entity.setSelectedRoomCleaning(dto.getSelectedRoomCleaning());
        entity.setNoMopZones(dto.getNoMopZones());
    }
    private void updateSensor(SensorDTO dto, Robot robot) {
        if (dto == null) {
            robot.setSensor(null);
            return;
        }

        Sensor entity = robot.getSensor();

        if (entity == null) {
            entity = new Sensor();
            robot.setSensor(entity);
        }

        entity.setCarpetBoost(dto.getCarpetBoost());
        entity.setCliffSensor(dto.getCliffSensor());
        entity.setDirtSensor(dto.getDirtSensor());
        entity.setFullDustbinSensor(dto.getFullDustbinSensor());
    }
    private void updateOther(OtherSpecificationsDTO dto, Robot robot) {
        if (dto == null) {
            robot.setOtherSpecifications(null);
            return;
        }

        OtherSpecifications entity = robot.getOtherSpecifications();

        if (entity == null) {
            entity = new OtherSpecifications();
            robot.setOtherSpecifications(entity);
        }

        entity.setWeight(dto.getWeight());
        entity.setWidth(dto.getWidth());
        entity.setHeight(dto.getHeight());
        entity.setInTheBox(dto.getInTheBox());
        entity.setReleaseDate(dto.getReleaseDate());
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

                    // Fix main image
                    dto.setImage(buildFullImageUrl(dto.getImage()));

                    // Fix consumable images
                    if (dto.getConsumables() != null) {
                        dto.getConsumables().forEach(consumable -> {
                            if (consumable.getImages() != null) {
                                consumable.setImages(
                                        consumable.getImages().stream()
                                                .map(this::buildFullImageUrl)
                                                .toList()
                                );
                            }
                        });
                    }

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
