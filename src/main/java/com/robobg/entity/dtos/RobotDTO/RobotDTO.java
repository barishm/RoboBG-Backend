package com.robobg.entity.dtos.RobotDTO;

import com.robobg.entity.dtos.ConsumableDTO.ConsumableTitleDTO;
import lombok.Data;

import java.util.List;


@Data
public class RobotDTO {
    private static final String IMAGE_BASE_URL = "https://api.barishm.com/images/";

    private Long id;
    private String brand;
    private String model;
    private String image;
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
    private List<PurchaseLinkDTO> purchaseLinks;
    private Integer qnaCount;
    private List<ConsumableTitleDTO> consumables;

}
