package com.team01.carsharingapp.dto.car.response;

import java.math.BigDecimal;

public record CarDto(
        Long id,
        String model,
        String brand,
        String type,
        int amountAvailable,
        BigDecimal dailyFee) {
}
