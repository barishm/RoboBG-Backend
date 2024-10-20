package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;

@Data
public class BatteryDTO {
    private Long id;
    private Integer batteryCapacity;
    private String batteryLife;
    private String chargingTime;
    private String ratedPower;
}
