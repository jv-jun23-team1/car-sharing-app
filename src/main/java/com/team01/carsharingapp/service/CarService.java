package com.team01.carsharingapp.service;

import com.team01.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.team01.carsharingapp.dto.car.response.CarDto;
import java.util.List;

public interface CarService {
    CarDto save(CreateCarRequestDto request);

    List<CarDto> getAll();

    CarDto getById(Long id);

    CarDto update(Long id, CreateCarRequestDto request);

    void delete(Long id);
}
