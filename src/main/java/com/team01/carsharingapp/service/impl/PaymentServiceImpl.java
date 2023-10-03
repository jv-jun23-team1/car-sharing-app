package com.team01.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.mapper.PaymentMapper;
import com.team01.carsharingapp.model.Payment;
import com.team01.carsharingapp.service.PaymentService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String CONVERT_CENT = "100";
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDto createPayment(PaymentRequestDto paymentRequestDto) {
        Stripe.apiKey = stripeSecretKey;
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((paymentRequestDto.price()
                        .multiply(new BigDecimal(CONVERT_CENT)).longValue()))
                .setCurrency(paymentRequestDto.currency())
                .setDescription("Car rental payment")
                .addPaymentMethodType("cart")
                .putMetadata("type", paymentRequestDto.type())
                .build();
        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            String paymentIntentId = paymentIntent.getId();
            Payment payment = new Payment();
            BigDecimal amount = new BigDecimal(params.getAmount());
            BigDecimal convertCent = new BigDecimal(CONVERT_CENT);
            payment.setPrice(amount.divide(convertCent, 2, RoundingMode.HALF_UP));
            payment.setType(Payment.Type.valueOf(paymentRequestDto.type()));

        } catch (StripeException e) {
            e.getMessage();
        }
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
