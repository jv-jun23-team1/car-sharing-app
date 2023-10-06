package com.coffee.jedi.carsharingapp.service;

import com.coffee.jedi.carsharingapp.dto.payment.PaymentDto;
import com.coffee.jedi.carsharingapp.dto.payment.PaymentRequestDto;
import com.coffee.jedi.carsharingapp.model.Payment;
import java.util.List;

public interface PaymentService {
    PaymentDto createPayment(PaymentRequestDto requestDto);

    List<PaymentDto> getPaymentsByUserId(Long userId);

    Payment getPaymentBySessionId(String sessionId);

    void save(Payment payment);

    List<PaymentDto> getAllPayments();

    boolean setPaymentSuccessStatus(String sessionId);

    String getCurrency();

    PaymentDto paymentRenewal(Long rentalId);
}
