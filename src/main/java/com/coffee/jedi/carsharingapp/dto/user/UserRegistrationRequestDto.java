package com.coffee.jedi.carsharingapp.dto.user;

import com.coffee.jedi.carsharingapp.validator.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldMatch(first = "password", second = "repeatPassword",
        message = "passwords must match")
public class UserRegistrationRequestDto {
    @NotBlank(message = "required field")
    @Size(min = 4, max = 50, message = "size must be between 4 and 255 character")
    @Email(message = "invalid email format")
    private String email;

    @NotBlank(message = "required field")
    @Size(min = 8, max = 50, message = "size must be between 8 and 50 character")
    private String password;

    @NotBlank(message = "required field")
    @Size(min = 8, max = 50, message = "size must be between 8 and 50 character")
    private String repeatPassword;

    @NotBlank(message = "required field")
    @Size(min = 2, max = 50, message = "size must be between 2 and 50 character")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "size must be between 2 and 50 character")
    private String lastName;
}
