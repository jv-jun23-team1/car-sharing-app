package com.team01.carsharingapp.controller;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/payment")
public class PaymentController {
    private static final String PAYMENT_SUCCESS = "Payment successful!";
    private static final String PAYMENT_CANSEL = "Payment paused!";
    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentDto> getPayments(@RequestParam("user_id") Long userId) {
        return paymentService.getPaymentsByUserId(userId);
    }

    @PostMapping
    public PaymentDto createPayment(
            @RequestBody PaymentRequestDto requestDto) {
        return paymentService.createPayment(requestDto);
    }

    @GetMapping("/success")
    public String checkSuccessfulPayments() {
        return PAYMENT_SUCCESS;
    }

    @GetMapping("/cancel")
    public String returnPaymentPausedMessage() {
        return PAYMENT_CANSEL;
    }
}
