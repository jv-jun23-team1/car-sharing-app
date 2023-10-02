package com.team01.carsharingapp.service;

import com.team01.carsharingapp.model.Payment;

public interface PaymentService {
    Payment createPayment(String currency, int amount);
    Payment getPayments();
}
