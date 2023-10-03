package com.team01.carsharingapp.mapper;

import com.team01.carsharingapp.config.MapperConfig;
import com.team01.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.team01.carsharingapp.dto.user.UserRegistrationResponseDto;
import com.team01.carsharingapp.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    User toModel(UserRegistrationRequestDto requestDto);

    UserRegistrationResponseDto toResponseDto(User user);
}
