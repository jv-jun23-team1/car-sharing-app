package com.coffee.jedi.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coffee.jedi.carsharingapp.dto.car.response.CarDto;
import com.coffee.jedi.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.coffee.jedi.carsharingapp.dto.rental.RentalDto;
import com.coffee.jedi.carsharingapp.event.CreateRentalEvent;
import com.coffee.jedi.carsharingapp.mapper.RentalMapper;
import com.coffee.jedi.carsharingapp.model.Car;
import com.coffee.jedi.carsharingapp.model.Rental;
import com.coffee.jedi.carsharingapp.model.Role;
import com.coffee.jedi.carsharingapp.model.User;
import com.coffee.jedi.carsharingapp.repository.CarRepository;
import com.coffee.jedi.carsharingapp.repository.RentalRepository;
import com.coffee.jedi.carsharingapp.service.impl.RentalServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTests {
    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private PaymentService paymentService;

    @Test
    @DisplayName("Get rental dto by valid id")
    public void getById_validId_returnRentalDto() {
        Rental validRental = getValidRental();
        RentalDto expected = getRentalDtoFromRental();
        when(rentalRepository.findByIdWithFetch(anyLong())).thenReturn(Optional.of(validRental));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expected);

        RentalDto actual = rentalService.getById(getValidUser(), validRental.getId());

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Create rental by valid request")
    void create_validRequestDto_returnRentalDto() {
        Car car = getValidCar();
        Car returnedCar = getValidCar();
        returnedCar.setAmountAvailable(car.getAmountAvailable() - 1);
        Rental rental = getValidRental();
        RentalDto expected = getRentalDtoFromRental();

        when(paymentService.getPaymentsByUserId(anyLong())).thenReturn(List.of());
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(returnedCar);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expected);

        RentalDto actual = rentalService.create(getValidUser(), getCreateRentalRequestDto());

        assertEquals(expected, actual);
        verify(applicationEventPublisher).publishEvent(any(CreateRentalEvent.class));
    }

    @Test
    @DisplayName("Get rental dto list by valid request params")
    void getByUserIdAndStatus_validRequestForManager_returnRentalDtoList() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = getValidUser();
        Rental rental = getValidRental();
        List<Rental> rentals = List.of(rental);
        RentalDto rentalDto = getRentalDtoFromRental();
        List<RentalDto> expected = List.of(rentalDto);

        when(rentalRepository.findAllByUserIdAndStatus(anyLong(), anyBoolean(), eq(pageable)))
                .thenReturn(rentals);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        List<RentalDto> actual = rentalService.getByUserIdAndStatus(
                user, user.getId(), true, pageable);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get rental dto when user returns a car")
    void carReturnByRentalId_validId_returnRentalDto() {
        Rental rental = getValidRental();
        RentalDto expected = getRentalDtoFromRental();
        Car car = getValidCar();
        User user = getValidUser();

        when(rentalRepository.findByIdAndUserId(
                anyLong(), anyLong())).thenReturn(Optional.of(rental));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expected);

        RentalDto actual = rentalService.carReturnByRentalId(user, rental.getId());

        assertEquals(expected, actual);
    }

    private Rental getValidRental() {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCar(getValidCar());
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusDays(1));
        rental.setUser(getValidUser());
        return rental;
    }

    private RentalDto getRentalDtoFromRental() {
        Rental rental = getValidRental();
        return new RentalDto(
                rental.getId(),
                rental.getRentalDate(),
                rental.getReturnDate(),
                rental.getActualReturnDate(),
                getCarDtoFromCar(),
                rental.getUser().getId()
        );
    }

    private CreateRentalRequestDto getCreateRentalRequestDto() {
        Rental rental = getValidRental();
        return new CreateRentalRequestDto(
                rental.getReturnDate(),
                rental.getCar().getId()
        );
    }

    private Car getValidCar() {
        Car car = new Car();
        car.setId(1L);
        car.setType(Car.Type.UNIVERSAL);
        car.setModel("model");
        car.setBrand("brand");
        car.setAmountAvailable(1);
        car.setDailyFee(BigDecimal.TEN);
        return car;
    }

    private CarDto getCarDtoFromCar() {
        Car car = getValidCar();
        return new CarDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getBrand(),
                car.getAmountAvailable(),
                car.getDailyFee()
        );
    }

    private User getValidUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("example@gmail.com");
        user.setPassword("password");
        user.setFirstName("name");
        user.setLastName("surname");
        user.setRoles(Set.of(getManagerRole()));
        return user;
    }

    private Role getManagerRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.ROLE_MANAGER);
        return role;
    }
}
