package com.robobg.dtos.RobotDTO;

import lombok.Data;

@Data
public class CreateMostComparedDTO {
    private Long id;
    private Integer order;
    private Long robot1;
    private Long robot2;
    private Long robot3;
}
