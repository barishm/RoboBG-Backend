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
@Table(name = "controls")
public class Control {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduling")
    private String scheduling;

    @Column(name = "ir_rf_remote_control")
    private String  Ir_Rf_RemoteControl;

    @Column(name = "wifi_smartphone_app")
    private String wifiSmartphoneApp;

    @Column(name = "wifi_frequency_band")
    private String wifiFrequencyBand;

    @Column(name = "amazon_alexa_support")
    private String amazonAlexaSupport;

    @Column(name = "google_assistant_support")
    private String googleAssistantSupport;

    @Column(name = "magnetic_virtual_walls")
    private String magneticVirtualWalls;

}
