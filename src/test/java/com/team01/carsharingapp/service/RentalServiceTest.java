package com.team01.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.team01.carsharingapp.dto.car.response.CarDto;
import com.team01.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.team01.carsharingapp.dto.rental.RentalDto;
import com.team01.carsharingapp.mapper.RentalMapper;
import com.team01.carsharingapp.model.Car;
import com.team01.carsharingapp.model.Rental;
import com.team01.carsharingapp.model.Role;
import com.team01.carsharingapp.model.User;
import com.team01.carsharingapp.repository.CarRepository;
import com.team01.carsharingapp.repository.RentalRepository;
import com.team01.carsharingapp.service.impl.RentalServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;

    @Test
    public void getById_validId_returnRentalDto() {
        Rental validRental = getValidRental();
        RentalDto expected = getRentalDtoFromRental();
        when(rentalRepository.findById(anyLong())).thenReturn(Optional.of(validRental));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expected);

        RentalDto actual = rentalService.getById(getValidUser(), validRental.getId());

        assertEquals(expected, actual);
    }

    @Test
    void create_validRequestDto_returnRentalDto() {
        Car car = getValidCar();
        Car returnedCar = getValidCar();
        returnedCar.setAmountAvailable(car.getAmountAvailable() - 1);
        RentalDto expected = getRentalDtoFromRental();

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(returnedCar);
        when(rentalRepository.save(any(Rental.class))).thenReturn(getValidRental());
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expected);

        RentalDto actual = rentalService.create(getValidUser(), getCreateRentalRequestDto());

        assertEquals(expected, actual);
    }

    @Test
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

        List<RentalDto> actual = rentalService.getByUserIdAndStatus(user, user.getId(), true, pageable);

        assertEquals(expected, actual);
    }

    @Test
    void carReturnByRentalId_validId_returnRentalDto() {
        Rental rental = getValidRental();
        RentalDto expected = getRentalDtoFromRental();
        Car car = getValidCar();
        User user = getValidUser();

        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(rental));
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