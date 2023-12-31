package com.coffee.jedi.carsharingapp.service.impl;

import com.coffee.jedi.carsharingapp.dto.payment.PaymentDto;
import com.coffee.jedi.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.coffee.jedi.carsharingapp.dto.rental.RentalDto;
import com.coffee.jedi.carsharingapp.event.CreateRentalEvent;
import com.coffee.jedi.carsharingapp.exception.EntityNotFoundException;
import com.coffee.jedi.carsharingapp.exception.RentalException;
import com.coffee.jedi.carsharingapp.mapper.RentalMapper;
import com.coffee.jedi.carsharingapp.model.Car;
import com.coffee.jedi.carsharingapp.model.Payment;
import com.coffee.jedi.carsharingapp.model.Rental;
import com.coffee.jedi.carsharingapp.model.Role;
import com.coffee.jedi.carsharingapp.model.User;
import com.coffee.jedi.carsharingapp.repository.CarRepository;
import com.coffee.jedi.carsharingapp.repository.RentalRepository;
import com.coffee.jedi.carsharingapp.service.PaymentService;
import com.coffee.jedi.carsharingapp.service.RentalService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final PaymentService paymentService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public RentalDto getById(User user, Long rentalId) {
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
    public RentalDto create(User user, CreateRentalRequestDto requestDto) {
        checkRentalAbility(user);
        checkTime(requestDto.returnDate());
        Rental rental = new Rental();
        rental.setCar(getAvailableCarFromDb(requestDto.carId()));
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(requestDto.returnDate());
        rental = rentalRepository.save(rental);
        applicationEventPublisher
                .publishEvent(CreateRentalEvent.of(this, rental));
        return rentalMapper.toDto(rental);
    }

    @Override
    public List<RentalDto> getByUserIdAndStatus(
            User user, Long userId, boolean isActive, Pageable pageable
    ) {
        List<Rental> rentals;
        if (isManager(user)) {
            rentals = getRentalsByUserIdAndStatus(userId, isActive, pageable);
        } else {
            if (!Objects.equals(userId, user.getId())) {
                throw new RentalException("User role hasn't opportunity for this");
            }
            rentals = rentalRepository
                    .findAllByUserIdAndStatus(user.getId(), isActive, pageable);
        }
        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public RentalDto carReturnByRentalId(User user, Long id) {
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

    private void checkRentalAbility(User user) {
        Optional<PaymentDto> overdue = paymentService.getPaymentsByUserId(user.getId()).stream()
                .filter(p -> p.getStatus().equals(Payment.Status.PENDING))
                .findFirst();
        if (overdue.isPresent()) {
            throw new RentalException(
                    "User has bill pending, can't rent new car until pay for bill. UserId = "
                            + user.getId()
            );
        }
    }

    private List<Rental> getRentalsByUserIdAndStatus(
            Long userId, boolean isActive, Pageable pageable) {
        return userId == null
                ? rentalRepository.findAllByStatus(isActive, pageable) :
                rentalRepository.findAllByUserIdAndStatus(userId, isActive, pageable);
    }

    private Car getAvailableCarFromDb(Long carId) {
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
