package com.team01.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank(message = "required field")
        @Size(min = 4, max = 255, message = "size must be between 4 and 255 character")
        @Email(message = "must match the email format")
        String email,
        @NotBlank(message = "required field")
        @Size(min = 8, max = 50, message = "size must be between 8 and 50 character")
        String password
) {
}
