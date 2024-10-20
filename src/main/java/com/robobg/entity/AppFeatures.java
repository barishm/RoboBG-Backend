package com.robobg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_features")
public class AppFeatures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "real_time_tracking")
    private Boolean realTimeTracking;

    @Column(name = "digital_blocked_areas")
    private Boolean digitalBlockedAreas;

    @Column(name = "zoned_cleaning")
    private Boolean zonedCleaning;

    @Column(name = "multi_floor_maps")
    private Boolean multiFloorMaps;

    @Column(name = "manual_movement_control")
    private Boolean manualMovementControl;

    @Column(name = "selected_room_cleaning")
    private Boolean selectedRoomCleaning;

    @Column(name = "no_mop_zones")
    private Boolean noMopZones;

}
