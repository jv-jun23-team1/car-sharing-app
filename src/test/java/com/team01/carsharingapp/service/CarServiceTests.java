package com.team01.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.team01.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.team01.carsharingapp.dto.car.response.CarDto;
import com.team01.carsharingapp.exception.EntityNotFoundException;
import com.team01.carsharingapp.mapper.CarMapper;
import com.team01.carsharingapp.model.Car;
import com.team01.carsharingapp.repository.CarRepository;
import com.team01.carsharingapp.service.impl.CarServiceImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    private static final int ONCE = 1;
    private static final Long ID_ONE = 1L;
    private static final Long INCORRECT_ID = 42L;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Save car by correct data")
    public void save_AllCorrectData_CorrectDtoReturned() {
        //given
        CreateCarRequestDto requestDto = createValidCreateCarRequestDto();
        Car expectedCarWithoutId = getCarFromCreateCarRequestDto(requestDto);
        Car expectedCarWithId = getCarFromCreateCarRequestDto(requestDto);
        expectedCarWithId.setId(ID_ONE);
        CarDto expected = getCarDtoFromCar(expectedCarWithId);

        when(carMapper.toEntity(requestDto)).thenReturn(expectedCarWithoutId);
        when(carRepository.save(expectedCarWithoutId)).thenReturn(expectedCarWithId);
        when(carMapper.toDto(expectedCarWithId)).thenReturn(expected);

        //when
        CarDto actual = carService.save(requestDto);

        //then
        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(ONCE)).save(expectedCarWithoutId);
        verifyNoMoreInteractions(carRepository);
        verify(carMapper, times(ONCE)).toEntity(requestDto);
        verify(carMapper, times(ONCE)).toDto(expectedCarWithId);
        verifyNoMoreInteractions(carMapper);
    }

    @Test
    @DisplayName("Show list of 1 car")
    public void getAll_OneCar_ReturnsCorrectList() {
        //given
        Car car = createValidCar();
        List<Car> cars = List.of(car);
        CarDto carDto = getCarDtoFromCar(car);

        when(carRepository.findAll()).thenReturn(cars);
        when(carMapper.toDto(car)).thenReturn(carDto);

        //when
        List<CarDto> actual = carService.getAll();

        //then
        List<CarDto> expected = new ArrayList<>();
        expected.add(carDto);
        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(ONCE)).findAll();
        verifyNoMoreInteractions(carRepository);
        verify(carMapper, times(ONCE)).toDto(car);
        verifyNoMoreInteractions(carMapper);
    }

    @Test
    @DisplayName("Get car by correct id")
    public void getById_WithValidId_CorrectDtoReturned() {
        //given
        Car car = createValidCar();
        CarDto expected = getCarDtoFromCar(car);

        when(carRepository.findById(ID_ONE)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(expected);

        //when
        CarDto actual = carService.getById(ID_ONE);

        //then
        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(ONCE)).findById(ID_ONE);
        verifyNoMoreInteractions(carRepository);
        verify(carMapper, times(ONCE)).toDto(car);
        verifyNoMoreInteractions(carMapper);
    }

    @Test
    @DisplayName("Try to get car by incorrect id")
    public void getById_WithInValidId_ExceptionThrown() {
        //given

        when(carRepository.findById(INCORRECT_ID)).thenReturn(Optional.empty());

        //when
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> carService.getById(INCORRECT_ID)
        );
        //then
        String expected = "Can't find car by id: " + INCORRECT_ID;
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(ONCE)).findById(INCORRECT_ID);
        verifyNoMoreInteractions(carRepository);
        verifyNoInteractions(carMapper);
    }

    @Test
    @DisplayName("Update car by correct id")
    public void update_WithValidId_CorrectDtoReturned() {
        //given
        CreateCarRequestDto request = createValidCreateCarRequestDto();
        Car oldCar = createValidCar();
        oldCar.setId(ID_ONE);
        Car updatedCar = getCarFromCreateCarRequestDto(request);
        updatedCar.setId(ID_ONE);
        CarDto expected = getCarDtoFromCar(oldCar);

        when(carRepository.findById(ID_ONE)).thenReturn(Optional.of(oldCar));
        when(carRepository.save(updatedCar)).thenReturn(updatedCar);
        when(carMapper.toDto(updatedCar)).thenReturn(expected);

        //when
        CarDto actual = carService.update(ID_ONE, request);

        //then
        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(ONCE)).findById(ID_ONE);
        verify(carRepository, times(ONCE)).save(updatedCar);
        verifyNoMoreInteractions(carRepository);
        verify(carMapper, times(ONCE)).toDto(updatedCar);
        verifyNoMoreInteractions(carMapper);
    }

    @Test
    @DisplayName("Try to update car by incorrect id")
    public void update_WithInValidId_ExceptionThrown() {
        //given
        CreateCarRequestDto request = createValidCreateCarRequestDto();
        String expected = "Can't find car by id: " + INCORRECT_ID;

        when(carRepository.findById(INCORRECT_ID)).thenReturn(Optional.empty());

        //when
        Throwable exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> carService.update(INCORRECT_ID, request));

        //then
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
        verify(carRepository, times(ONCE)).findById(INCORRECT_ID);
        verifyNoMoreInteractions(carRepository);
        verifyNoInteractions(carMapper);
    }

    @Test
    @DisplayName("Delete car by correct id")
    public void deleteById_CorrectId_Success() {
        //given
        when(carRepository.existsById(ID_ONE)).thenReturn(true);
        doNothing().when(carRepository).deleteById(ID_ONE);

        //when
        carService.delete(ID_ONE);

        //then
        verify(carRepository, times(ONCE)).existsById(ID_ONE);
        verify(carRepository, times(ONCE)).deleteById(ID_ONE);
        verifyNoMoreInteractions(carRepository);
        verifyNoInteractions(carMapper);
    }

    @Test
    @DisplayName("Try to delete car by incorrect id")
    public void deleteById_InCorrectId_ThrowsException() {
        //given
        String excepted = "Can't find car by id: " + INCORRECT_ID;

        when(carRepository.existsById(INCORRECT_ID)).thenReturn(false);

        //when
        Throwable exception = assertThrows(EntityNotFoundException.class,
                () -> carService.delete(INCORRECT_ID));

        //then
        String actual = exception.getMessage();
        Assertions.assertEquals(excepted, actual);
        verify(carRepository, times(ONCE)).existsById(INCORRECT_ID);
        verifyNoMoreInteractions(carRepository);
        verifyNoInteractions(carMapper);
    }

    private Car createValidCar() {
        Car car = new Car();
        car.setModel("City");
        car.setBrand("Honda");
        car.setType(Car.Type.valueOf("SEDAN"));
        car.setAmountAvailable(3);
        car.setDailyFee(BigDecimal.valueOf(5));
        return car;
    }

    private CreateCarRequestDto createValidCreateCarRequestDto() {
        return new CreateCarRequestDto("RAV4", "TOYOTA", "SUV", 1, BigDecimal.valueOf(3));
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
