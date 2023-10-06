package com.coffee.jedi.carsharingapp.service.impl;

import com.coffee.jedi.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.coffee.jedi.carsharingapp.dto.car.response.CarDto;
import com.coffee.jedi.carsharingapp.exception.EntityNotFoundException;
import com.coffee.jedi.carsharingapp.mapper.CarMapper;
import com.coffee.jedi.carsharingapp.model.Car;
import com.coffee.jedi.carsharingapp.repository.CarRepository;
import com.coffee.jedi.carsharingapp.service.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional
    public CarDto save(CreateCarRequestDto request) {
        Car car = carMapper.toEntity(request);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    @Transactional
    public List<CarDto> getAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CarDto getById(Long id) {
        return carMapper.toDto(carById(id));
    }

    @Override
    @Transactional
    public CarDto update(Long id, CreateCarRequestDto request) {
        Car car = carById(id);
        updateValues(car, request);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find car by id: " + id);
        }
        carRepository.deleteById(id);
    }

    private Car carById(Long id) {
        return carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: " + id)
        );
    }

    private void updateValues(Car car, CreateCarRequestDto request) {
        car.setModel(request.model());
        car.setBrand(request.brand());
        car.setType(Car.Type.valueOf(request.type()));
        car.setAmountAvailable(request.amountAvailable());
        car.setDailyFee(request.dailyFee());
    }
}
