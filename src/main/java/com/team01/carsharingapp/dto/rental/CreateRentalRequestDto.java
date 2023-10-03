package com.team01.carsharingapp.dto.rental;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateRentalRequestDto(
        @NotNull
        LocalDate returnDate,
        @NotNull
        Long carId
) {
}
