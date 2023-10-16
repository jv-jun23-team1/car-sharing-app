package com.coffee.jedi.carsharingapp.service;

import com.coffee.jedi.carsharingapp.dto.user.UpdateUserDto;
import com.coffee.jedi.carsharingapp.dto.user.UpdateUserRoleDto;
import com.coffee.jedi.carsharingapp.dto.user.UserDto;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.coffee.jedi.carsharingapp.exception.RegistrationException;
import com.coffee.jedi.carsharingapp.model.User;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    UserDto getInfo(User user);

    UserDto updateRoles(Long id, UpdateUserRoleDto userRoleDto);

    UserDto update(UpdateUserDto updateUserDto, User user);
}
