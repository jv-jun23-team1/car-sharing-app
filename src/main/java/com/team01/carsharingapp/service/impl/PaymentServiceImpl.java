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
import org.springframework.transaction.annotation.Transactional;

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
        payment.setSessionUrl(sessionInfo.getSessionUrl());
        payment.setSessionId(sessionInfo.getSessionId());

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
        return paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Payment with session id: "
                        + sessionId + " not found!")
        );
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public boolean setPaymentSuccessStatus(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        payment.setStatus(Payment.Status.PAID);
        save(payment);
        return true;
    }

    private BigDecimal calculateRentalCost(LocalDate startDate,
                                           LocalDate endDate,
                                           BigDecimal dailyRate) {
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        return dailyRate.multiply(BigDecimal.valueOf(numberOfDays));
    }

    private Payment createPaymentEntity(Rental rentalById,
                                        PaymentRequestDto requestDto) {
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
