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
    private String realTimeTracking;

    @Column(name = "digital_blocked_areas")
    private String digitalBlockedAreas;

    @Column(name = "zoned_cleaning")
    private String zonedCleaning;

    @Column(name = "multi_floor_maps")
    private String multiFloorMaps;

    @Column(name = "manual_movement_control")
    private String manualMovementControl;

    @Column(name = "selected_room_cleaning")
    private String selectedRoomCleaning;

    @Column(name = "no_mop_zones")
    private String noMopZones;

}
