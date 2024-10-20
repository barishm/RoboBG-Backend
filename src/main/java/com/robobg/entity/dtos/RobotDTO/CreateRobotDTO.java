package com.robobg.entity.dtos.RobotDTO;


import lombok.Data;

@Data
public class CreateRobotDTO {
    private Long id;
    private String brand;
    private String model;
    private Boolean bests;
    private Boolean mapping;
    private String mappingSensorType;
    private Boolean highPrecisionMap;
    private Boolean frontCamera;
    private Boolean rechargeResume;
    private Boolean autoDockAndRecharge;
    private String noiseLevel;
    private Boolean display;
    private String sideBrushes;
    private Boolean voicePrompts;
    private CleaningFeaturesDTO cleaningFeatures;
    private MoppingFeaturesDTO moppingFeatures;
    private BatteryDTO battery;
    private ControlDTO control;
    private AppFeaturesDTO appFeatures;
    private SensorDTO sensor;
    private OtherSpecificationsDTO otherSpecifications;
}
