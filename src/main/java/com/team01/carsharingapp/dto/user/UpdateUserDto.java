package com.team01.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {
    @NotBlank(message = "required field")
    @Size(min = 5, max = 50, message = "size must be between 5 and 50 character")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "required field")
    @Size(min = 8, max = 50, message = "size must be between 8 and 50 character")
    private String password;

    @NotBlank(message = "required field")
    @Size(min = 2, max = 50, message = "size must be between 2 and 50 character")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "size must be between 2 and 50 character")
    private String lastName;

}
