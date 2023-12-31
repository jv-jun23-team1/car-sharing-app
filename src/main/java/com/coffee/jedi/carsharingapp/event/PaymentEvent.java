package com.coffee.jedi.carsharingapp.event;

import com.coffee.jedi.carsharingapp.model.Payment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentEvent extends ApplicationEvent {
    private final Payment payment;

    public PaymentEvent(Object source, Payment payment) {
        super(source);
        this.payment = payment;
    }

    public static PaymentEvent of(Object source, Payment payment) {
        return new PaymentEvent(source, payment);
    }
}
