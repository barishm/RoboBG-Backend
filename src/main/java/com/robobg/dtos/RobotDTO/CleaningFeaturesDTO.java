package com.robobg.dtos.RobotDTO;

import lombok.Data;

@Data
public class CleaningFeaturesDTO {
    private Long id;
    private Integer suctionPower;
    private String cleaningArea;
    private Integer dustbinCapacity;
    private String disposableDustBagCapacity;
    private String autoDirtDisposal;
    private String barrierCrossHeight;
    private String hepaFilter;
    private String washableFilter;
}
