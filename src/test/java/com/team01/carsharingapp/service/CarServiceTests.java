package com.team01.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.team01.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.team01.carsharingapp.dto.car.response.CarDto;
import com.team01.carsharingapp.exception.EntityNotFoundException;
import com.team01.carsharingapp.mapper.CarMapper;
import com.team01.carsharingapp.model.Car;
import com.team01.carsharingapp.repository.CarRepository;
import com.team01.carsharingapp.service.impl.CarServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CarServiceTests {
    private static final Long ID_ONE = 1L;
    private static final Long INCORRECT_ID = 42L;
    private static final String CAR_NOT_FOUND_MESSAGE = "Can't find car by id: ";
    private static final String TEST_CAR_MODEL = "City";
    private static final String TEST_CAR_BRAND = "Honda";
    private static final Car.Type TEST_CAR_TYPE = Car.Type.valueOf("SEDAN");
    private static final int TEST_CAR_AMOUNT = 3;
    private static final BigDecimal TEST_CAR_FEE = BigDecimal.valueOf(5);
    private static final String TEST_DTO_MODEL = "RAV4";
    private static final String TEST_DTO_BRAND = "TOYOTA";
    private static final String TEST_DTO_TYPE = "SUV";
    private static final int TEST_DTO_AMOUNT = 1;
    private static final BigDecimal TEST_DTO_FEE = BigDecimal.valueOf(3);

    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Save car by correct data")
    public void save_AllCorrectData_CorrectDtoReturned() {
        CreateCarRequestDto requestDto = createValidCreateCarRequestDto();
        Car expectedCarWithoutId = getCarFromCreateCarRequestDto(requestDto);
        Car expectedCarWithId = getCarFromCreateCarRequestDto(requestDto);
        expectedCarWithId.setId(ID_ONE);
        CarDto expected = getCarDtoFromCar(expectedCarWithId);

        when(carMapper.toEntity(requestDto)).thenReturn(expectedCarWithoutId);
        when(carRepository.save(expectedCarWithoutId)).thenReturn(expectedCarWithId);
        when(carMapper.toDto(expectedCarWithId)).thenReturn(expected);

        CarDto actual = carService.save(requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Show list of 1 car")
    public void getAll_OneCar_ReturnsCorrectList() {
        Car car = createValidCar();
        List<Car> cars = List.of(car);
        CarDto carDto = getCarDtoFromCar(car);
        List<CarDto> expected = List.of(carDto);

        when(carRepository.findAll()).thenReturn(cars);
        when(carMapper.toDto(car)).thenReturn(carDto);

        List<CarDto> actual = carService.getAll();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get car by correct id")
    public void getById_WithValidId_CorrectDtoReturned() {
        Car car = createValidCar();
        CarDto expected = getCarDtoFromCar(car);

        when(carRepository.findById(ID_ONE)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(expected);

        CarDto actual = carService.getById(ID_ONE);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Try to get car by incorrect id")
    public void getById_WithInValidId_ExceptionThrown() {
        String expected = CAR_NOT_FOUND_MESSAGE + INCORRECT_ID;

        when(carRepository.findById(INCORRECT_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> carService.getById(INCORRECT_ID)
        );
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verifyNoInteractions(carMapper);
    }

    @Test
    @DisplayName("Update car by correct id")
    public void update_WithValidId_CorrectDtoReturned() {
        CreateCarRequestDto request = createValidCreateCarRequestDto();
        Car oldCar = createValidCar();
        oldCar.setId(ID_ONE);
        Car updatedCar = getCarFromCreateCarRequestDto(request);
        updatedCar.setId(ID_ONE);
        CarDto expected = getCarDtoFromCar(oldCar);

        when(carRepository.findById(ID_ONE)).thenReturn(Optional.of(oldCar));
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);
        when(carMapper.toDto(updatedCar)).thenReturn(expected);

        CarDto actual = carService.update(ID_ONE, request);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Try to update car by incorrect id")
    public void update_WithInValidId_ExceptionThrown() {
        CreateCarRequestDto request = createValidCreateCarRequestDto();
        String expected = CAR_NOT_FOUND_MESSAGE + INCORRECT_ID;

        when(carRepository.findById(INCORRECT_ID)).thenReturn(Optional.empty());

        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> carService.update(INCORRECT_ID, request));
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verifyNoInteractions(carMapper);
    }

    @Test
    @DisplayName("Delete car by correct id")
    public void deleteById_CorrectId_Success() {
        when(carRepository.existsById(ID_ONE)).thenReturn(true);
        doNothing().when(carRepository).deleteById(ID_ONE);

        carService.delete(ID_ONE);

        verifyNoInteractions(carMapper);
    }

    @Test
    @DisplayName("Try to delete car by incorrect id")
    public void deleteById_InCorrectId_ThrowsException() {
        String excepted = CAR_NOT_FOUND_MESSAGE + INCORRECT_ID;

        when(carRepository.existsById(INCORRECT_ID)).thenReturn(false);

        Throwable exception = assertThrows(EntityNotFoundException.class,
                () -> carService.delete(INCORRECT_ID));
        String actual = exception.getMessage();

        assertEquals(excepted, actual);
        verifyNoInteractions(carMapper);
    }

    private Car createValidCar() {
        Car car = new Car();
        car.setModel(TEST_CAR_MODEL);
        car.setBrand(TEST_CAR_BRAND);
        car.setType(TEST_CAR_TYPE);
        car.setAmountAvailable(TEST_CAR_AMOUNT);
        car.setDailyFee(TEST_CAR_FEE);
        return car;
    }

    private CreateCarRequestDto createValidCreateCarRequestDto() {
        return new CreateCarRequestDto(
                TEST_DTO_MODEL,
                TEST_DTO_BRAND,
                TEST_DTO_TYPE,
                TEST_DTO_AMOUNT,
                TEST_DTO_FEE);
    }

    private Car getCarFromCreateCarRequestDto(CreateCarRequestDto requestDto) {
        Car car = new Car();
        car.setModel(requestDto.model());
        car.setBrand(requestDto.brand());
        car.setType(Car.Type.valueOf(requestDto.type()));
        car.setAmountAvailable(requestDto.amountAvailable());
        car.setDailyFee(requestDto.dailyFee());
        return car;
    }

    private CarDto getCarDtoFromCar(Car car) {
        return new CarDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getType().name(),
                car.getAmountAvailable(),
                car.getDailyFee());
    }
}
