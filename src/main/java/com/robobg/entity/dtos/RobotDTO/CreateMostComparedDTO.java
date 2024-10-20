package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;

@Data
public class CreateMostComparedDTO {
    private Integer order;
    private Long robot1;
    private Long robot2;
    private Long robot3;
}
