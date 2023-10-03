package com.team01.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.team01.carsharingapp.service.StripeService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {
    private static final String CONVERT_CENT = "100";
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public void pay(BigDecimal totalPrice, String currency, String type) {
        Stripe.apiKey = stripeSecretKey;
        Long totalAmount = totalPrice.multiply(BigDecimal.valueOf(100)).longValue();
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("successUrl")
                .setCancelUrl("cancelUrl")
                .setCurrency(currency)
                .build();
        try {
            Session session = Session.create(params);
        }
        catch (StripeException e) {
            e.getMessage();
        }
    }
}
