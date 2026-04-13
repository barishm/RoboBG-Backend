package com.robobg.dtos.RobotDTO;

import lombok.Data;

@Data
public class SensorDTO {
    private Long id;
    private String carpetBoost;
    private String cliffSensor;
    private String dirtSensor;
    private String fullDustbinSensor;
}
