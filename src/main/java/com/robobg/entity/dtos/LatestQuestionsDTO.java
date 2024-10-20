package com.robobg.entity.dtos;

import com.robobg.entity.dtos.RobotDTO.RobotModelDTO;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LatestQuestionsDTO {
    private Long id;
    private AuthorDTO author;
    private LocalDateTime createTime;
    private List<AnswerAuthorsDTO> answers;
    private RobotModelDTO robot;
}
