package com.robobg.entity.oldDtos;

import lombok.Data;

import java.util.List;

@Data
public class RobotResponse {
    private List<?> content;
    private int pageNo;
    private int totalPages;
    private boolean last;
}
