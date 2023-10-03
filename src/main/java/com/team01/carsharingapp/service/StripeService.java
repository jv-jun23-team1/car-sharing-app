package com.team01.carsharingapp.service;

import java.math.BigDecimal;

public interface StripeService {
    StripeDto pay(BigDecimal totalPrice, String currency, String type);
}
