package com.robobg.entity.dtos.ConsumableDTO;

import lombok.Data;

import java.util.List;

@Data
public class CreateConsumableDTO {
    private Long id;
    private String title;
    private String description;
    private List<Long> compatibleRobotIds;
}
