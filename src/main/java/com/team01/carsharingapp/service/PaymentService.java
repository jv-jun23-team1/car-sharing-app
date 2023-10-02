package com.team01.carsharingapp.service;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.model.Payment;
import java.util.List;

public interface PaymentService {
    PaymentDto createPayment(PaymentRequestDto paymentRequestDto);
    PaymentDto getPayments();

    List<PaymentDto> getPaymentsByUserId(Long userId);
}
