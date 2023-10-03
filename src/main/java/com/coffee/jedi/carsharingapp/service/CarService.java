package com.coffee.jedi.carsharingapp.service;

import com.coffee.jedi.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.coffee.jedi.carsharingapp.dto.car.response.CarDto;
import java.util.List;

public interface CarService {
    CarDto save(CreateCarRequestDto request);

    List<CarDto> getAll();

    CarDto getById(Long id);

    CarDto update(Long id, CreateCarRequestDto request);

    void delete(Long id);
}
