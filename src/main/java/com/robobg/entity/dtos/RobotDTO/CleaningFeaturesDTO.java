package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;

@Data
public class CleaningFeaturesDTO {
    private Long id;
    private Integer suctionPower;
    private String cleaningArea;
    private Integer dustbinCapacity;
    private String disposableDustBagCapacity;
    private Boolean autoDirtDisposal;
    private String barrierCrossHeight;
    private Boolean hepaFilter;
    private Boolean washableFilter;
}
