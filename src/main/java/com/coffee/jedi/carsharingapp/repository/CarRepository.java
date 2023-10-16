package com.coffee.jedi.carsharingapp.repository;

import com.coffee.jedi.carsharingapp.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
