package com.coffee.jedi.carsharingapp.mapper;

import com.coffee.jedi.carsharingapp.config.MapperConfig;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.coffee.jedi.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.coffee.jedi.carsharingapp.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    User toModel(UserRegistrationRequestDto requestDto);

    UserRegistrationResponseDto toResponseDto(User user);
}
