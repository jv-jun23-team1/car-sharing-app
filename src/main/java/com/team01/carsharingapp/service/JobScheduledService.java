package com.team01.carsharingapp.service;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.model.Payment;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JobScheduledService {
    private final PaymentService paymentService;
    private final StripeService stripeService;

    @Scheduled(fixedRate = 600000)
    public void checkSessionValidPayment() {
        List<PaymentDto> allPayments = paymentService.getAllPayments();
        for (PaymentDto paymentDto : allPayments) {
            String sessionId = paymentDto.getSessionId();
            if (stripeService.isExpired(sessionId)) {
                Payment paymentBySessionId = paymentService.getPaymentBySessionId(sessionId);
                paymentBySessionId.setStatus(Payment.Status.EXPIRED);
                paymentService.save(paymentBySessionId);
            }
        }
    }
}
