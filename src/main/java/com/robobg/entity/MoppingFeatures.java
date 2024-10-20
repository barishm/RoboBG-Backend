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
@Table(name = "mopping_features")
public class MoppingFeatures {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wet_mopping")
    private Boolean wetMopping;

    @Column(name = "electric_water_flow_control")
    private Boolean electricWaterFlowControl;

    @Column(name = "water_tank_capacity")
    private Integer waterTankCapacity;

    @Column(name = "vibrating_mopping_pad")
    private Boolean vibratingMoppingPad;

    @Column(name = "auto_mop_lifting")
    private Boolean autoMopLifting;

    @Column(name = "auto_water_tank_refilling")
    private Boolean autoWaterTankRefilling;

    @Column(name = "auto_mop_washing")
    private Boolean autoMopWashing;
}
