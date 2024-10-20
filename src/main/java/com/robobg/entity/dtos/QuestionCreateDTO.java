package com.robobg.entity.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuestionCreateDTO {
    private Long id;
    private Long robotId;
    private String authorUsername;
    @NotBlank
    @NotNull
    @Size(min = 5,max = 300)
    private String text;
}
