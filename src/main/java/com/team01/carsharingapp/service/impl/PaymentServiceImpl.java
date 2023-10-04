package com.team01.carsharingapp.service.impl;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.mapper.PaymentMapper;
import com.team01.carsharingapp.model.Car;
import com.team01.carsharingapp.model.Rental;
import com.team01.carsharingapp.repository.payment.PaymentRepository;
import com.team01.carsharingapp.service.PaymentService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDto createPayment(PaymentRequestDto requestDto) {
        //create test entity rental
        Rental currentRental = new Rental();
        currentRental.setId(requestDto.rentalId());
        Car currentCar = new Car();
        currentCar.setDailyFee(BigDecimal.valueOf(20.0));
        currentRental.setCar(currentCar);
        currentRental.setRentalDate(LocalDate.now());
        currentRental.setReturnDate(LocalDate.now().plusDays(3));
        //Values for stripe
        BigDecimal totalPrice = calculateRentalCost(
                currentRental.getRentalDate(),
                currentRental.getReturnDate(),
                currentRental.getCar().getDailyFee());
        String currentCurrency = requestDto.currency();
        String currentType = requestDto.type();
        return new PaymentDto();
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

    private BigDecimal calculateRentalCost(LocalDate startDate,
                                           LocalDate endDate,
                                           BigDecimal dailyRate) {
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        return dailyRate.multiply(BigDecimal.valueOf(numberOfDays));
    }
}
