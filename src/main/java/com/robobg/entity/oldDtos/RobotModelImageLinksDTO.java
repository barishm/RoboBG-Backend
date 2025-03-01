package com.robobg.entity.oldDtos;

import com.robobg.entity.dtos.RobotDTO.PurchaseLinkDTO;
import lombok.Data;

import java.util.List;

@Data
public class RobotModelImageLinksDTO {
    private Long id;
    private String model;
    private String image;
    private Integer qnaCount;
    private List<PurchaseLinkDTO> purchaseLinks;
}

