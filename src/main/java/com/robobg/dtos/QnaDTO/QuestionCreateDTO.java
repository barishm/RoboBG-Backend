package com.robobg.dtos.QnaDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionCreateDTO {

    private Long id;

    @NotNull
    private Long robotId;

    private String anonymousName;

    private String avatar;

    @NotBlank
    @NotNull
    @Size(min = 5,max = 300)
    private String text;
}
