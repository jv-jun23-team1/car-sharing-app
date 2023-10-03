package com.team01.carsharingapp.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaymentRequestDto(@NotBlank(message = "id can't be null")
                                @Min(value = 0, message = "id can't be less than 0")
                                Long rentalId,
                                @NotBlank(message = "currency can't be null")
                                @Size(min = 2, max = 3,
                                        message = "currency size can't be less "
                                                + "than 2 and more than 3")
                                String currency,
                                @NotBlank(message = "type can't be null")
                                String type) {
}
