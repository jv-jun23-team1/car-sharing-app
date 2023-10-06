package com.coffee.jedi.carsharingapp.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDto(@NotNull(message = "id can't be null")
                                @Min(value = 0, message = "id can't be less than 0")
                                Long rentalId,
                                @NotBlank(message = "type can't be null")
                                String type) {
}
