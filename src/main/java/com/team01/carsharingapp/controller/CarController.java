package com.team01.carsharingapp.controller;

import com.team01.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.team01.carsharingapp.dto.car.response.CarDto;
import com.team01.carsharingapp.service.CarService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public CarDto add(@RequestBody @Valid CreateCarRequestDto request) {
        return carService.save(request);
    }

    @GetMapping
    public List<CarDto> getAll() {
        return carService.getAll();
    }

    @GetMapping("/{id}")
    public CarDto getById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public CarDto update(@PathVariable Long id, @RequestBody @Valid CreateCarRequestDto request) {
        return carService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        carService.delete(id);
    }
}
