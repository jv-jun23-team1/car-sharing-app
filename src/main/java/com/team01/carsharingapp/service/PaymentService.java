package com.team01.carsharingapp.service;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import java.util.List;

public interface PaymentService {
    String createPayment(PaymentRequestDto requestDto);

    PaymentDto getPayments();

    List<PaymentDto> getPaymentsByUserId(Long userId);
}
