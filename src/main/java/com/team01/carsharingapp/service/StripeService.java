package com.team01.carsharingapp.service;

import com.team01.carsharingapp.dto.stripe.StripeDto;
import com.team01.carsharingapp.model.Payment;

public interface StripeService {
    StripeDto pay(Payment payment,
                  String currency);
}
