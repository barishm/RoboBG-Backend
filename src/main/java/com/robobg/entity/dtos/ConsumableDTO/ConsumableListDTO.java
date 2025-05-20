package com.robobg.entity.dtos.ConsumableDTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ConsumableListDTO {
    private static final String IMAGE_BASE_URL = "https://api.barishm.com/images/consumables/";

    private Long id;
    private String title;
    private String description;
    private String price;
    private List<String> images = new ArrayList<>();
    private List<RobotModelImageDTO> robots = new ArrayList<>();



    public List<String> getImages() {
        if (images == null) return null;
        return images.stream()
                .map(img -> IMAGE_BASE_URL + img)
                .collect(Collectors.toList());
    }
}
