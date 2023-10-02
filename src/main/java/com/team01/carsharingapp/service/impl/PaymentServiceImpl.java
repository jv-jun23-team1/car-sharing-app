package com.team01.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.model.Payment;
import com.team01.carsharingapp.service.PaymentService;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public PaymentDto createPayment(PaymentRequestDto paymentRequestDto) {
        Stripe.apiKey = stripeSecretKey;
        /*PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setDescription("Car rental payment")
                .addPaymentMethodType("cart")
                .putMetadata("type", type.name())
                .build();
        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            String paymentIntentId = paymentIntent.getId();
            Payment payment = new Payment();
        } catch (StripeException e) {
            e.getMessage();
        }*/
        return null;
    }

    @Override
    public PaymentDto getPayments() {
        return null;
    }

    @Override
    public List<PaymentDto> getPaymentsByUserId(Long userId) {
        return null;
    }
}
