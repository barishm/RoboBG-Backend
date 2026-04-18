package com.robobg.dtos.RobotDTO;


import lombok.Data;
import java.util.List;

@Data
public class CreateRobotDTO {
    private Long id;
    private String brand;
    private String model;
    private Integer bests;
    private String mapping;
    private String mappingSensorType;
    private String highPrecisionMap;
    private String frontCamera;
    private String rechargeResume;
    private String autoDockAndRecharge;
    private String noiseLevel;
    private String display;
    private String sideBrushes;
    private String voicePrompts;
    private CleaningFeaturesDTO cleaningFeatures;
    private MoppingFeaturesDTO moppingFeatures;
    private BatteryDTO battery;
    private ControlDTO control;
    private AppFeaturesDTO appFeatures;
    private SensorDTO sensor;
    private OtherSpecificationsDTO otherSpecifications;
    private List<PurchaseLinkDTO> purchaseLinks;
}
