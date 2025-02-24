package com.robobg.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "robots")
public class Robot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand",nullable = false)
    private String brand;

    @Column(name = "model",unique = true,nullable = false)
    private String model;

    @Column(name = "image")
    private String image;

    @Column(name = "bests")
    private Boolean bests;

    @Column
    private Boolean mapping;

    @Column(name = "mapping_sensor_type")
    private String mappingSensorType;

    @Column(name = "high_precision_map")
    private Boolean highPrecisionMap;

    @Column(name = "front_camera")
    private Boolean frontCamera;

    @Column(name = "recharge_resume")
    private Boolean rechargeResume;

    @Column(name = "auto_dock_and_recharge")
    private Boolean autoDockAndRecharge;

    @Column(name = "noise_level")
    private String noiseLevel;

    @Column
    private Boolean display;

    @Column(name = "side_brushes")
    private String sideBrushes;

    @Column(name = "voice_prompts")
    private Boolean voicePrompts;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.ALL})
    @JoinColumn(name = "cleaning_features_id",referencedColumnName = "id")
    private CleaningFeatures cleaningFeatures;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.ALL})
    @JoinColumn(name = "mopping_features_id",referencedColumnName = "id")
    private MoppingFeatures moppingFeatures;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.ALL})
    @JoinColumn(name = "batteries_id",referencedColumnName = "id")
    private Battery battery;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.ALL})
    @JoinColumn(name = "controls_id",referencedColumnName = "id")
    private Control control;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.ALL})
    @JoinColumn(name = "app_features_id",referencedColumnName = "id")
    private AppFeatures appFeatures;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.ALL})
    @JoinColumn(name = "sensors_id",referencedColumnName = "id")
    private Sensor sensor;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.ALL})
    @JoinColumn(name = "other_specifications_id",referencedColumnName = "id")
    private OtherSpecifications otherSpecifications;

    @OneToMany(mappedBy = "robot", cascade = CascadeType.ALL)
    private List<PurchaseLink> purchaseLinks;

    @OneToMany(mappedBy = "robot", cascade = CascadeType.ALL)
    private List<Question> questions;

    @Column(name = "qna_count")
    private Integer qnaCount;

    @ManyToMany(mappedBy = "compatibleRobots")
    private Set<Consumable> consumables = new HashSet<>();
}
