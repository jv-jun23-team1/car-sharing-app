package com.coffee.jedi.carsharingapp.mapper;

import com.coffee.jedi.carsharingapp.config.MapperConfig;
import com.coffee.jedi.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.coffee.jedi.carsharingapp.dto.car.response.CarDto;
import com.coffee.jedi.carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Car toEntity(CreateCarRequestDto request);

    CarDto toDto(Car car);
}
