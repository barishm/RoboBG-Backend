package com.robobg.dtos.RobotDTO;

import lombok.Data;

@Data
public class MostComparedDTO {
    private Long id;
    private Integer order;
    private RobotModelDTO robot1;
    private RobotModelDTO robot2;
    private RobotModelDTO robot3;
}
