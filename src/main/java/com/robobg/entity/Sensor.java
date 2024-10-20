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
@Table(name = "sensors")
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "carpet_boost")
    private Boolean carpetBoost;

    @Column(name = "cliff_sensor")
    private Boolean cliffSensor;

    @Column(name = "dirt_sensor")
    private Boolean dirtSensor;

    @Column(name = "full_dustbin_sensor")
    private Boolean fullDustbinSensor;
}
