package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;

@Data
public class AppFeaturesDTO {
    private Long id;
    private Boolean realTimeTracking;
    private Boolean digitalBlockedAreas;
    private Boolean zonedCleaning;
    private Boolean multiFloorMaps;
    private Boolean manualMovementControl;
    private Boolean selectedRoomCleaning;
    private Boolean noMopZones;
}
