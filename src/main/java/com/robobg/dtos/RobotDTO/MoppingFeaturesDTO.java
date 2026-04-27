package com.robobg.dtos.RobotDTO;

import lombok.Data;

@Data
public class MoppingFeaturesDTO {
    private Long id;
    private String wetMopping;
    private String electricWaterFlowControl;
    private Integer waterTankCapacity;
    private String vibratingMoppingPad;
    private String autoMopLifting;
    private String autoWaterTankRefilling;
    private String autoMopWashing;
    private String spinningMops;
    private String washingMopsWithWarmWater;
    private String dryingTheMops;
    private String automaticDetergentDosing;
}
