package com.team01.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank(message = "required field")
        @Email(message = "must match the email format")
        String email,
        @NotBlank(message = "required field")
        String password
) {
}
