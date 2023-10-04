package com.team01.carsharingapp.dto.user;

import java.util.Set;
import lombok.Data;

@Data
public class UserDto {
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private Set<Long> roleIds;
}
