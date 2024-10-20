package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;

@Data
public class CreatePurchaseLinkDTO {
    private Long id;
    private Long robotId;
    private String name;
    private String link;
}
