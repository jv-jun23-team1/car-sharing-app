package com.coffee.jedi.carsharingapp.security;

import com.coffee.jedi.carsharingapp.dto.user.UserLoginRequestDto;
import com.coffee.jedi.carsharingapp.dto.user.UserLoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password())
        );
        return new UserLoginResponseDto(jwtUtil.generateToken(requestDto.email()));
    }
}
