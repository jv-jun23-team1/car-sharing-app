package com.team01.carsharingapp.mapper;

import com.team01.carsharingapp.config.MapperConfig;
import com.team01.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.team01.carsharingapp.dto.car.response.CarDto;
import com.team01.carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Car toEntity(CreateCarRequestDto request);

    CarDto toDto(Car car);
}
