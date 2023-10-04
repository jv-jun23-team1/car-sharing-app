package com.team01.carsharingapp.service.impl;

import com.stripe.exception.StripeException;
import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.dto.stripe.StripeDto;
import com.team01.carsharingapp.mapper.PaymentMapper;
import com.team01.carsharingapp.model.Car;
import com.team01.carsharingapp.model.Payment;
import com.team01.carsharingapp.model.Rental;
import com.team01.carsharingapp.repository.PaymentRepository;
import com.team01.carsharingapp.repository.RentalRepository;
import com.team01.carsharingapp.service.PaymentService;
import com.team01.carsharingapp.service.StripeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String CURRENCY = "usd";
    private PaymentRepository paymentRepository;
    private RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;

    @Override
    public PaymentDto createPayment(PaymentRequestDto requestDto) {
        //create test entity rental
        Rental currentRental = new Rental();
        currentRental.setId(requestDto.rentalId());
        Car currentCar = new Car();
        currentCar.setModel("AudiV8");
        currentCar.setDailyFee(BigDecimal.valueOf(20.0));
        currentRental.setCar(currentCar);
        currentRental.setRentalDate(LocalDate.now());
        currentRental.setReturnDate(LocalDate.now().plusDays(3));
        //Values for stripe
        BigDecimal totalPrice = calculateRentalCost(
                currentRental.getRentalDate(),
                currentRental.getReturnDate(),
                currentRental.getCar().getDailyFee());
        String currentType = requestDto.type();
        Payment payment = paymentMapper.toEntity(requestDto);
        payment.setRental(currentRental);
        payment.setType(Payment.Type.valueOf(requestDto.type()));
        payment.setStatus(Payment.Status.PENDING);
        payment.setPrice(totalPrice);
        try {
            StripeDto sessionInfo = stripeService.pay(currentCar.getModel(),
                    totalPrice, CURRENCY, currentType);
            payment.setSessionUrl(sessionInfo.getSessionUrl());
            payment.setSessionId(sessionInfo.getSessionId());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public PaymentDto getPayments() {
        return new PaymentDto();
    }

    @Override
    public List<PaymentDto> getPaymentsByUserId(Long userId) {
        return paymentRepository.findAllByUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public Payment getPaymentBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId);
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    private BigDecimal calculateRentalCost(LocalDate startDate,
                                           LocalDate endDate,
                                           BigDecimal dailyRate) {
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        return dailyRate.multiply(BigDecimal.valueOf(numberOfDays));
    }
}
