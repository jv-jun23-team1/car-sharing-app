package com.coffee.jedi.carsharingapp.service;

import com.coffee.jedi.carsharingapp.dto.stripe.StripeDto;
import com.coffee.jedi.carsharingapp.model.Payment;

public interface StripeService {
    StripeDto pay(Payment payment,
                  String currency);

    boolean isPaid(String id);

    boolean isExpired(String id);
}
