package com.robobg.dtos.ConsumableDTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ConsumableListDTO {

    private Long id;
    private String title;
    private String description;
    private String price;
    private List<String> images = new ArrayList<>();
    private List<RobotModelImageDTO> robots = new ArrayList<>();

}
