package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;

@Data
public class MoppingFeaturesDTO {
    private Long id;
    private Boolean wetMopping;
    private Boolean electricWaterFlowControl;
    private Integer waterTankCapacity;
    private Boolean vibratingMoppingPad;
    private Boolean autoMopLifting;
    private Boolean autoWaterTankRefilling;
    private Boolean autoMopWashing;
}
