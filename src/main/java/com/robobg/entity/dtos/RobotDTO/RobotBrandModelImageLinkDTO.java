package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;

import java.util.List;

@Data
public class RobotBrandModelImageLinkDTO {
    private Long id;
    private String brand;
    private String model;
    private String image;
    private Integer qnaCount;
    private List<PurchaseLinkDTO> purchaseLinks;
}
