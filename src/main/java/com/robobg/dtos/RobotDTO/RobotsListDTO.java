package com.robobg.dtos.RobotDTO;

import lombok.Data;

import java.util.List;

@Data
public class RobotsListDTO {
    private Long id;
    private String brand;
    private String model;
    private String image;
    private Integer qnaCount;
    private Boolean bests;
    private List<PurchaseLinkDTO> purchaseLinks;
}
