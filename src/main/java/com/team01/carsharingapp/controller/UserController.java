package com.team01.carsharingapp.controller;

import com.team01.carsharingapp.dto.user.UpdateUserDto;
import com.team01.carsharingapp.dto.user.UpdateUserRoleDto;
import com.team01.carsharingapp.dto.user.UserDto;
import com.team01.carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    UserDto getInfo() {
        return userService.getInfo();
    }

    @PutMapping("{id}/role")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update user roles")
    UserDto updateRoles(@PathVariable Long id,
                        @Valid @RequestBody UpdateUserRoleDto userRoleDto) {
        return userService.updateRoles(id, userRoleDto);
    }

    @PutMapping("/me")
    @Operation(summary = "Update user", description = "Update user. "
            + "Validation included.")
    UserDto update(@Valid @RequestBody UpdateUserDto updateUserDto) {
        return userService.update(updateUserDto);
    }
}
