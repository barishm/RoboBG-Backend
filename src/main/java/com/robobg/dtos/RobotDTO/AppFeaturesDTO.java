package com.robobg.dtos.RobotDTO;

import lombok.Data;

@Data
public class AppFeaturesDTO {
    private Long id;
    private String realTimeTracking;
    private String digitalBlockedAreas;
    private String zonedCleaning;
    private String multiFloorMaps;
    private String manualMovementControl;
    private String selectedRoomCleaning;
    private String noMopZones;
}
