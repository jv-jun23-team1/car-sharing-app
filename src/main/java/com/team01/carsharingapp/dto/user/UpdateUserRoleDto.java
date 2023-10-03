package com.team01.carsharingapp.dto.user;

import java.util.Set;

public record UpdateUserRoleDto(
        Set<Long> roleIds) {
}
