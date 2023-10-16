package com.coffee.jedi.carsharingapp.controller;

import com.coffee.jedi.carsharingapp.dto.user.UserLoginRequestDto;
import com.coffee.jedi.carsharingapp.dto.user.UserLoginResponseDto;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.coffee.jedi.carsharingapp.exception.RegistrationException;
import com.coffee.jedi.carsharingapp.security.AuthenticationService;
import com.coffee.jedi.carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication",
        description = "Endpoints for registration and login")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Register a user")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
