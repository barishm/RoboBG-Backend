package com.robobg.dtos.RobotDTO;

import lombok.Data;

import java.util.List;

@Data
public class RobotsListDTO {
    private static final String IMAGE_BASE_URL = "https://api.barishm.com/images/";

    private Long id;
    private String brand;
    private String model;
    private String image;
    private Integer qnaCount;
    private List<PurchaseLinkDTO> purchaseLinks;
}
