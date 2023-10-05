package com.team01.carsharingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team01.carsharingapp.dto.car.request.CreateCarRequestDto;
import com.team01.carsharingapp.dto.car.response.CarDto;
import com.team01.carsharingapp.model.Car;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        teardown(dataSource);
        setup(dataSource);
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Verify add() method works for CarController")
    @Sql(scripts = {
            "classpath:database/cars/remove-created-test-car.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addCar_ValidRequestDto_ReturnCarDto() throws Exception {
        Car car = new Car(
                null,
                "some_model",
                "some_brand",
                Car.Type.SEDAN,
                10,
                BigDecimal.valueOf(12.99),
                false
        );
        CreateCarRequestDto requestDto = createCarRequestDto(car);
        CarDto expected = createCarDto(car);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class
        );
        Assertions.assertNotNull(actual.id());
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "customer")
    @Test
    @DisplayName("Verify getAll() method works for CarController")
    void getAllCars_WithoutPageable_ReturnListCarDto() throws Exception {
        List<CarDto> expected = getAllFromDb().stream()
                .map(this::createCarDto)
                .toList();

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/cars")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        CarDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CarDto[].class
        );
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "customer")
    @Test
    @DisplayName("Verify getById() method works for CarController")
    void getCarById_ValidCarId_ReturnCarDto() throws Exception {
        Car book = getAllFromDb().get(0);
        CarDto expected = createCarDto(book);
        Long expectedBookId = book.getId();

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/cars/" + expectedBookId)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class
        );

        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Verify update() method works for CarController")
    @Sql(scripts = {
            "classpath:database/cars/add-for-update-test-car.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/cars/remove-for-update-test-car.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCar_ValidRequestDto_ReturnCarDto() throws Exception {
        Car car = new Car(
                4L,
                "update",
                "update",
                Car.Type.UNIVERSAL,
                1,
                BigDecimal.valueOf(13.22),
                false
        );
        CreateCarRequestDto requestDto = createCarRequestDto(car);
        Long expectedBookId = car.getId();
        CarDto expected = createCarDto(car);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/cars/" + expectedBookId)
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                                .content(jsonRequest)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        CarDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Verify delete() method works for CarController")
    @Sql(scripts = {
            "classpath:database/cars/add-for-update-test-car.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ValidCarId_Successful() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/cars/4")
                                .contentType(MediaType.APPLICATION_JSON.getMediaType())
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @SneakyThrows
    static void setup(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/add-three-cars.sql")
            );
        }
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/remove-three-cars.sql")
            );
        }
    }

    private CarDto createCarDto(Car car) {
        return new CarDto(
                car.getId(),
                car.getModel(),
                car.getBrand(),
                car.getType().name(),
                car.getAmountAvailable(),
                car.getDailyFee()
        );
    }

    private CreateCarRequestDto createCarRequestDto(Car car) {
        return new CreateCarRequestDto(
                car.getModel(),
                car.getBrand(),
                car.getType().name(),
                car.getAmountAvailable(),
                car.getDailyFee()
        );
    }

    private List<Car> getAllFromDb() {
        return List.of(
                new Car(
                        1L,
                        "AUDI",
                        "A6",
                        Car.Type.SEDAN,
                        10,
                        BigDecimal.valueOf(20.22),
                        false
                ),
                new Car(
                        2L,
                        "Toyota",
                        "Corolla",
                        Car.Type.HATCHBACK,
                        12,
                        BigDecimal.valueOf(10.22),
                        false
                ),
                new Car(
                        3L,
                        "Acura",
                        "RDX",
                        Car.Type.UNIVERSAL,
                        1,
                        BigDecimal.valueOf(13.22),
                        false
                )
        );
    }
}
