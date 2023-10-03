package com.team01.carsharingapp.controller;

import com.team01.carsharingapp.dto.user.UpdateUserDto;
import com.team01.carsharingapp.dto.user.UpdateUserRoleDto;
import com.team01.carsharingapp.dto.user.UserDto;
import com.team01.carsharingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    UserDto getInfo() {
        return userService.getInfo();
    }

    @PutMapping("{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    UserDto updateRoles(@PathVariable Long id,
                        @RequestBody UpdateUserRoleDto userRoleDto) {
        return userService.updateRoles(id, userRoleDto);
    }

    @PutMapping("/me")
    UserDto update(@RequestBody UpdateUserDto updateUserDto) {
        return userService.update(updateUserDto);
    }
}
