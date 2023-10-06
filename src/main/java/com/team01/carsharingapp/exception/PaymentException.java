package com.team01.carsharingapp.exception;

import com.stripe.exception.StripeException;

public class PaymentException extends RuntimeException {
    public PaymentException(String s) {
        super(s);
    }

    public PaymentException(String s, StripeException e) {
        super(s, e);
    }
}
