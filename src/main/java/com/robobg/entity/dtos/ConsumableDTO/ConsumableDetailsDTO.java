package com.robobg.entity.dtos.ConsumableDTO;

import com.robobg.entity.dtos.RobotDTO.RobotModelDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConsumableDetailsDTO {
    private String title;
    private String description;
    private String price;

    private List<String> images = new ArrayList<>();

    private List<RobotModelImageDTO> robots;
}
