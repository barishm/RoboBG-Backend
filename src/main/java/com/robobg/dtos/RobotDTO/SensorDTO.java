package com.robobg.dtos.RobotDTO;

import lombok.Data;

@Data
public class SensorDTO {
    private Long id;
    private Boolean carpetBoost;
    private Boolean cliffSensor;
    private Boolean dirtSensor;
    private Boolean fullDustbinSensor;
}
