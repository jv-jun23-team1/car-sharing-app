package com.team01.carsharingapp.service;

import com.team01.carsharingapp.dto.user.UpdateUserDto;
import com.team01.carsharingapp.dto.user.UpdateUserRoleDto;
import com.team01.carsharingapp.dto.user.UserDto;
import com.team01.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.team01.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.team01.carsharingapp.exception.RegistrationException;
import com.team01.carsharingapp.model.User;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    User getCurrentUser();

    UserDto getInfo();

    UserDto updateRoles(Long id, UpdateUserRoleDto userRoleDto);

    UserDto update(UpdateUserDto updateUserDto);
}
