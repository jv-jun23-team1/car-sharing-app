package com.team01.carsharingapp.service.impl;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.dto.stripe.StripeDto;
import com.team01.carsharingapp.exception.EntityNotFoundException;
import com.team01.carsharingapp.mapper.PaymentMapper;
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
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;

    @Override
    public PaymentDto createPayment(PaymentRequestDto requestDto) {
        Rental rentalById = rentalRepository
                .findByIdWithFetch(requestDto.rentalId()).orElseThrow(
                        () -> new EntityNotFoundException("Rental with id: "
                        + requestDto.rentalId() + " not exist")
        );
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
            throw new IllegalArgumentException("rentalById "
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
