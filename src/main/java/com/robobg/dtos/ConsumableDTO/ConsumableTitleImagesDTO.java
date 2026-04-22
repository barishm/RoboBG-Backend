package com.robobg.dtos.ConsumableDTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConsumableTitleImagesDTO {
    private Long id;
    private String title;
    private List<String> images = new ArrayList<>();
}
