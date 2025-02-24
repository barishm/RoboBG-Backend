package com.robobg.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Consumables")
public class Consumable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToMany
    @JoinTable(
            name = "robot_consumables",
            joinColumns = @JoinColumn(name = "consumable_id"),
            inverseJoinColumns = @JoinColumn(name = "robot_id")
    )
    private Set<Robot> compatibleRobots = new HashSet<>();


    @ElementCollection
    @CollectionTable(name = "consumable_images", joinColumns = @JoinColumn(name = "consumable_id"))
    @Column(name = "image_path")
    private List<String> images = new ArrayList<>();

}
