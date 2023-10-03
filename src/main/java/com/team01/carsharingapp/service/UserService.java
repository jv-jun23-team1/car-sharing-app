package com.team01.carsharingapp.service;

import com.team01.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.team01.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.team01.carsharingapp.exception.RegistrationException;
import com.team01.carsharingapp.model.User;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    User getCurrentUser();
}
