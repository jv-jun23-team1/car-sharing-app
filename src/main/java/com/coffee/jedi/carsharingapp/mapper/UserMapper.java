package com.coffee.jedi.carsharingapp.mapper;

import com.coffee.jedi.carsharingapp.config.MapperConfig;
import com.coffee.jedi.carsharingapp.dto.user.UpdateUserDto;
import com.coffee.jedi.carsharingapp.dto.user.UserDto;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.coffee.jedi.carsharingapp.model.Role;
import com.coffee.jedi.carsharingapp.model.User;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);

    User toModel(UpdateUserDto requestDto);

    UserRegistrationResponseDto toResponseDto(User user);

    @Mapping(target = "roleIds", ignore = true)
    UserDto toDto(User user);

    @AfterMapping
    default void setRoleIds(@MappingTarget UserDto userDto, User user) {
        if (user.getRoles() != null) {
            userDto.setRoleIds(user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toSet()));
        }
    }
}
