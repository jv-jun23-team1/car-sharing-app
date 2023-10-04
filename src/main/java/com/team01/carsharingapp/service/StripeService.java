package com.team01.carsharingapp.service;

import com.stripe.exception.StripeException;
import com.team01.carsharingapp.dto.stripe.StripeDto;
import java.math.BigDecimal;

public interface StripeService {
    StripeDto pay(String productName,
                  BigDecimal totalPrice,
                  String currency,
                  String type)
            throws StripeException;
}
