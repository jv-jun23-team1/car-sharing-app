package com.team01.carsharingapp.service.impl;

import com.team01.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.team01.carsharingapp.dto.rental.RentalDto;
import com.team01.carsharingapp.exception.EntityNotFoundException;
import com.team01.carsharingapp.exception.RentalException;
import com.team01.carsharingapp.mapper.RentalMapper;
import com.team01.carsharingapp.model.Car;
import com.team01.carsharingapp.model.Rental;
import com.team01.carsharingapp.model.Role;
import com.team01.carsharingapp.model.User;
import com.team01.carsharingapp.repository.CarRepository;
import com.team01.carsharingapp.repository.RentalRepository;
import com.team01.carsharingapp.service.RentalService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;

    @Override
    public RentalDto getById(Long rentalId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Rental rental;
        if (isManager(user)) {
            rental = rentalRepository.findByIdWithFetch(rentalId).orElseThrow(() ->
                    new EntityNotFoundException(
                            "Can't find rental by id = " + rentalId)
            );
        } else {
            rental = rentalRepository.findByIdAndUserId(rentalId, user.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find rental by id = " + rentalId
                                    + " and user id = " + user.getId()
                    )
            );
        }
        return rentalMapper.toDto(rental);
    }

    @Override
    @Transactional
    public RentalDto create(CreateRentalRequestDto requestDto) {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        checkTime(requestDto.returnDate());
        Rental rental = new Rental();
        rental.setCar(createCar(requestDto.carId()));
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(requestDto.returnDate());
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalDto> getByUserIdAndStatus(
            Long userId, boolean isActive, Pageable pageable
    ) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Rental> rentals;
        if (isManager(user)) {
            if (userId == null) {
                rentals = rentalRepository.findAllByStatus(isActive, pageable);
            } else {
                rentals = rentalRepository.findAllByUserIdAndStatus(userId, isActive, pageable);
            }
        } else {
            if (userId == null || userId.equals(user.getId())) {
                rentals = rentalRepository
                        .findAllByUserIdAndStatus(user.getId(), isActive, pageable);
            } else {
                throw new RentalException("User role hasn't opportunity for this");
            }
        }
        return rentals.stream().map(rentalMapper::toDto).toList();
    }

    @Transactional
    @Override
    public RentalDto returnCarByRentalId(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Rental rental = rentalRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() ->
                new EntityNotFoundException(
                        "Can't find rental by id = " + id + " and user id = " + user.getId()
                )
        );
        rental.setActualReturnDate(LocalDate.now());
        Car car = rental.getCar();
        car.setAmountAvailable(car.getAmountAvailable() + 1);
        carRepository.save(car);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    private Car createCar(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(() ->
                new EntityNotFoundException("Can't find car by id = " + carId));
        if (car.getAmountAvailable() < 1) {
            throw new RentalException("No free cars by car id = " + carId);
        }
        car.setAmountAvailable(car.getAmountAvailable() - 1);
        return carRepository.save(car);
    }

    private void checkTime(LocalDate localDate) {
        if (localDate.isBefore(LocalDate.now())) {
            throw new RentalException("Invalid return date = " + localDate);
        }
    }

    private boolean isManager(User user) {
        Optional<Role.RoleName> manager = user.getRoles().stream()
                .map(Role::getName)
                .filter(r -> r == Role.RoleName.ROLE_MANAGER)
                .findFirst();
        return manager.isPresent();
    }
}
