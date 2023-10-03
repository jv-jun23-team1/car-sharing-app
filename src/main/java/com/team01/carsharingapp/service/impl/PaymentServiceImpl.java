package com.team01.carsharingapp.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.mapper.PaymentMapper;
import com.team01.carsharingapp.model.Car;
import com.team01.carsharingapp.model.Payment;
import com.team01.carsharingapp.model.Rental;
import com.team01.carsharingapp.service.PaymentService;
import com.team01.carsharingapp.service.StripeService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final Object rentalService;

    @Override
    public PaymentDto createPayment(PaymentRequestDto paymentRequestDto) {
        Rental currentRental = new Rental();
        Car currentCar = new Car();
        currentCar.setDailyFee(BigDecimal.valueOf(20.0));
        currentRental.setCar(currentCar);
        currentRental.setRentalDate(LocalDate.now());
        currentRental.setReturnDate(LocalDate.now().plusDays(3));

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
