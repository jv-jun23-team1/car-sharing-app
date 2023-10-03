package com.team01.carsharingapp.service.impl;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.mapper.PaymentMapper;
import com.team01.carsharingapp.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentMapper paymentMapper;
    private final Object rentalService;

    @Override
    public PaymentDto createPayment(PaymentRequestDto requestDto) {
        return new PaymentDto();
    }

    @Override
    public PaymentDto getPayments() {
        return new PaymentDto();
    }

    @Override
    public List<PaymentDto> getPaymentsByUserId(Long userId) {
        return List.of(new PaymentDto());
    }
}
