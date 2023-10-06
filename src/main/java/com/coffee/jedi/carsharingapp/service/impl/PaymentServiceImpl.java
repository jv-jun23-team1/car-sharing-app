package com.coffee.jedi.carsharingapp.service.impl;

import com.coffee.jedi.carsharingapp.dto.payment.PaymentDto;
import com.coffee.jedi.carsharingapp.dto.payment.PaymentRequestDto;
import com.coffee.jedi.carsharingapp.dto.stripe.StripeDto;
import com.coffee.jedi.carsharingapp.event.PaymentEvent;
import com.coffee.jedi.carsharingapp.exception.EntityNotFoundException;
import com.coffee.jedi.carsharingapp.exception.PaymentException;
import com.coffee.jedi.carsharingapp.mapper.PaymentMapper;
import com.coffee.jedi.carsharingapp.model.Payment;
import com.coffee.jedi.carsharingapp.model.Rental;
import com.coffee.jedi.carsharingapp.repository.PaymentRepository;
import com.coffee.jedi.carsharingapp.repository.RentalRepository;
import com.coffee.jedi.carsharingapp.service.PaymentService;
import com.coffee.jedi.carsharingapp.service.StripeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String CURRENCY = "usd";
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public PaymentDto createPayment(PaymentRequestDto requestDto) {
        Rental rentalById = rentalRepository
                .findByIdWithFetch(requestDto.rentalId()).orElseThrow(
                        () -> new EntityNotFoundException("Rental with id: "
                                + requestDto.rentalId() + " not exist")
                );
        List<Payment> allPaymentByUserId = paymentRepository
                .findAllByUserId(rentalById.getUser().getId());
        if (!allPaymentByUserId.isEmpty()) {
            for (Payment currentPayment : allPaymentByUserId) {
                if (currentPayment.getStatus() == Payment.Status.PENDING) {
                    throw new PaymentException("You must pay previous rental - "
                            + paymentMapper.toDto(currentPayment).toString());
                }
            }
        }
        Payment payment = createPaymentEntity(rentalById, requestDto);

        StripeDto sessionInfo = stripeService.pay(payment, CURRENCY);
        if (sessionInfo == null
                || sessionInfo.getSessionUrl() == null
                || sessionInfo.getSessionId() == null) {
            throw new NullPointerException("Failed to create payment session with Stripe");
        }
        payment.setSessionUrl(sessionInfo.getSessionUrl());
        payment.setSessionId(sessionInfo.getSessionId());

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentDto> getPaymentsByUserId(Long userId) {
        if (userId == null) {
            throw new NullPointerException("userId cannot be null");
        }
        return paymentRepository.findAllByUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public Payment getPaymentBySessionId(String sessionId) {
        if (sessionId == null) {
            throw new NullPointerException("sessionId cannot be null");
        }
        return paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Payment with session id: "
                        + sessionId + " not found!")
        );
    }

    @Override
    public void save(Payment payment) {
        if (payment == null) {
            throw new NullPointerException("Payment cannot be null");
        }
        paymentRepository.save(payment);
    }

    @Override
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public boolean setPaymentSuccessStatus(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        payment.setStatus(Payment.Status.PAID);
        save(payment);
        applicationEventPublisher.publishEvent(PaymentEvent.of(this, payment));
        return true;
    }

    @Override
    public String getCurrency() {
        return CURRENCY;
    }

    @Override
    public PaymentDto paymentRenewal(Long rentalId) {
        Payment byRentalId = paymentRepository.findByRentalId(rentalId);
        StripeDto pay = stripeService.pay(byRentalId, CURRENCY);
        byRentalId.setSessionId(pay.getSessionId());
        byRentalId.setSessionUrl(pay.getSessionUrl());
        return paymentMapper.toDto(paymentRepository.save(byRentalId));
    }

    private BigDecimal calculateRentalCost(LocalDate startDate,
                                           LocalDate endDate,
                                           BigDecimal dailyRate) {
        if (startDate == null || endDate == null || dailyRate == null) {
            throw new NullPointerException("startDate, "
                    + "endDate, and dailyRate cannot be null");
        }
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        return dailyRate.multiply(BigDecimal.valueOf(numberOfDays));
    }

    private Payment createPaymentEntity(Rental rentalById,
                                        PaymentRequestDto requestDto) {
        if (rentalById == null) {
            throw new NullPointerException("rentalById "
                    + "cannot be null");
        }
        Payment payment = paymentMapper.toEntity(requestDto);
        BigDecimal totalPrice = calculateRentalCost(
                rentalById.getRentalDate(),
                rentalById.getReturnDate(),
                rentalById.getCar().getDailyFee());
        payment.setRental(rentalById);
        payment.setStatus(Payment.Status.PENDING);
        payment.setPrice(totalPrice);
        return payment;
    }
}
