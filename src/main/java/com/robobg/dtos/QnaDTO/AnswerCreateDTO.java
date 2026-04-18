package com.robobg.dtos.QnaDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnswerCreateDTO {

    @NotNull
    private Long questionId;

    @NotBlank
    @Size(min = 5, max = 300)
    private String text;

    private String avatar;
}
