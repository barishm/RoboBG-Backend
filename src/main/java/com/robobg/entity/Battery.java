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
@Table(name = "batteries")
public class Battery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "battery_capacity")
    private Integer batteryCapacity;

    @Column(name = "battery_life")
    private String batteryLife;

    @Column(name = "charging_time")
    private String chargingTime;

    @Column(name = "rated_power")
    private String ratedPower;
}
