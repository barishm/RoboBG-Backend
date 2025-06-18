package com.robobg.entity.dtos;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @Email
    private String email;
}
