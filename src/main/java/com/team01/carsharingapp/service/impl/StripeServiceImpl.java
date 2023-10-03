package com.team01.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.team01.carsharingapp.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {
    private static final String CONVERT_CENT = "100";
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public void pay() {
        Stripe.apiKey = stripeSecretKey;

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(0L)
                .setCurrency("usd")
                .setDescription("Car rental payment")
                .addPaymentMethodType("cart")
                .build();
        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            String paymentIntentId = paymentIntent.getId();
        } catch (StripeException e) {
            e.getMessage();
        }
    }
}
