package com.coffee.jedi.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateRentalRequestDto(
        @NotNull
        LocalDate returnDate,
        @NotNull
        Long carId
) {
}
