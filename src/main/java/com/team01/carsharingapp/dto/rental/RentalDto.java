package com.team01.carsharingapp.dto.rental;

import com.team01.carsharingapp.dto.car.response.CarDto;
import java.time.LocalDate;

public record RentalDto(
        Long id,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate,
        CarDto carDto,
        Long userId
) {
}
