package com.coffee.jedi.carsharingapp.dto.user;

import java.util.Set;

public record UpdateUserRoleDto(
        Set<Long> roleIds) {
}
