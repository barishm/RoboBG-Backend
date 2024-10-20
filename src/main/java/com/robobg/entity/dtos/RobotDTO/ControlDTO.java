package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;

@Data
public class ControlDTO {
    private Long id;
    private Boolean scheduling;
    private Boolean irRfRemoteControl;
    private Boolean wifiSmartphoneApp;
    private String wifiFrequencyBand;
    private Boolean amazonAlexaSupport;
    private Boolean googleAssistantSupport;
    private Boolean magneticVirtualWalls;
}
