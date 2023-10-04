package com.team01.carsharingapp.dto.user;

import com.team01.carsharingapp.validator.NotBlankIfNotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {
    //@NotBlank(message = "required field")
    //@Null
    @NotBlankIfNotNull
    @Size(min = 4, max = 255, message = "size must be between 4 and 255 character")
    @Email(message = "invalid email format")
    private String email;

    //@NotBlank(message = "required field")
    //@Null
    @NotBlankIfNotNull
    @Size(min = 8, max = 50, message = "size must be between 8 and 50 character")
    private String password;

    //@Null
    @NotBlankIfNotNull
    @Size(min = 2, max = 50, message = "size must be between 2 and 50 character")
    private String firstName;

    //@Null
    @NotBlankIfNotNull
    @Size(min = 2, max = 50, message = "size must be between 2 and 50 character")
    private String lastName;

}
