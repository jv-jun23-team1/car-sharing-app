package com.coffee.jedi.carsharingapp.mapper;

import com.coffee.jedi.carsharingapp.config.MapperConfig;
import com.coffee.jedi.carsharingapp.dto.rental.RentalDto;
import com.coffee.jedi.carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {CarMapper.class, UserMapper.class})
public interface RentalMapper {
    @Mapping(source = "car", target = "carDto")
    @Mapping(source = "user.id", target = "userId")
    RentalDto toDto(Rental rental);
}
