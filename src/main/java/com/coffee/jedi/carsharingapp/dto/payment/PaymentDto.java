package com.coffee.jedi.carsharingapp.dto.payment;

import com.coffee.jedi.carsharingapp.model.Payment;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentDto {
    private Long id;
    private Long rentalId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal price;
    private Payment.Status status;
    private Payment.Type type;
}
