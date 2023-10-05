package com.team01.carsharingapp.dto.car.request;

import com.team01.carsharingapp.model.Car;
import com.team01.carsharingapp.validator.EnumValue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateCarRequestDto(
        @NotBlank(message = "model can't be null")
        @Size(min = 2, max = 100, message = "model should be between 2 and 100 characters")
        String model,
        @NotBlank(message = "brand can't be null")
        @Size(min = 2, max = 100, message = "brand should be between 2 and 100 characters")
        String brand,
        @NotBlank(message = "type can't be null")
        @EnumValue(enumClass = Car.Type.class, message = "invalid enum Car.Type value")
        String type,
        @Min(value = 0, message = "amount available can't be less than 0")
        int amountAvailable,
        @NotNull(message = "daily fee can't be null")
        @Min(value = 0, message = "daily fee can't be less than 0")
        BigDecimal dailyFee) {
}
