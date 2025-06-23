package com.robobg.dtos.QnaDTO;

import com.robobg.dtos.RobotDTO.RobotModelDTO;
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
