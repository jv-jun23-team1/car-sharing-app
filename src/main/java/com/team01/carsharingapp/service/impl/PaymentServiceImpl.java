package com.team01.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.team01.carsharingapp.model.Payment;
import com.team01.carsharingapp.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;

public class PaymentServiceImpl implements PaymentService {
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public Payment createPayment(String currency, int amount) {
        Stripe.apiKey = stripeSecretKey;


        return null;
    }

    @Override
    public Payment getPayments() {
        return null;
    }
}
