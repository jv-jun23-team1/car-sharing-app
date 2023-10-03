package com.team01.carsharingapp.mapper;

import com.team01.carsharingapp.config.MapperConfig;
import com.team01.carsharingapp.dto.user.UpdateUserDto;
import com.team01.carsharingapp.dto.user.UserDto;
import com.team01.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.team01.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.team01.carsharingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(UpdateUserDto updateUserDto);

    UserRegistrationResponseDto toResponseDto(User user);

    @Mapping(target = "roleIds", ignore = true)
    UserDto toDto(User user);
}
