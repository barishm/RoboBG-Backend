package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OtherSpecificationsDTO {
    private Long id;
    private String weight;
    private String width;
    private String height;
    private String inTheBox;
    private LocalDate releaseDate;
}
