package com.team01.carsharingapp.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PaymentRequestDto(@NotBlank
                                Long rentalId,
                                @NotBlank
                                BigDecimal price,
                                @NotBlank
                                @Size(min = 2, max = 3)
                                String currency,
                                @NotBlank
                                String type) {
}
