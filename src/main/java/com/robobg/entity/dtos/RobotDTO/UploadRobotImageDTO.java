package com.robobg.entity.dtos.RobotDTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class UploadRobotImageDTO {
    private long id;
    private MultipartFile image;
}
