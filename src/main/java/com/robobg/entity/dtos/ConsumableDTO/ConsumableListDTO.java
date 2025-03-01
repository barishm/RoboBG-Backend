package com.robobg.entity.dtos.ConsumableDTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConsumableListDTO {
    private Long id;
    private String title;
    private String description;
    private List<String> images = new ArrayList<>();
    private List<RobotModelImageDTO> robots = new ArrayList<>();
}
