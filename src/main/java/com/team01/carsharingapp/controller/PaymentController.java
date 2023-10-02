package com.team01.carsharingapp.controller;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments(@RequestParam("user_id") Long userId) {
        List<PaymentDto> payments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentDto paymentDto = paymentService.createPayment(paymentRequestDto);
        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping("/success")
    public ResponseEntity<String> checkSuccessfulPayments() {
        // You can handle successful payments logic here, e.g., checking with Stripe
        return ResponseEntity.ok("Payment successful!");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> returnPaymentPausedMessage() {
        // Handle payment cancellation logic here, e.g., returning a message
        return ResponseEntity.ok("Payment paused");
    }
}
