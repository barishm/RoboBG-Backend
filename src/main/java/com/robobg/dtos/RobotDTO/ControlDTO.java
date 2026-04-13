package com.robobg.dtos.RobotDTO;

import lombok.Data;

@Data
public class ControlDTO {
    private Long id;
    private String scheduling;
    private String irRfRemoteControl;
    private String wifiSmartphoneApp;
    private String wifiFrequencyBand;
    private String amazonAlexaSupport;
    private String googleAssistantSupport;
    private String magneticVirtualWalls;
}
