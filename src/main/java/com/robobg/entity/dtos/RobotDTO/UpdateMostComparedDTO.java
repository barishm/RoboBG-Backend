package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;

@Data
public class UpdateMostComparedDTO {
    private Long id;
    private Integer order;
    private Long robot1;
    private Long robot2;
    private Long robot3;
}
