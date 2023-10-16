package com.coffee.jedi.carsharingapp.service;

import com.coffee.jedi.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.coffee.jedi.carsharingapp.dto.car.response.CarDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto save(CreateCarRequestDto request);

    List<CarDto> getAll(Pageable pageable);

    CarDto getById(Long id);

    CarDto update(Long id, CreateCarRequestDto request);

    void delete(Long id);
}
