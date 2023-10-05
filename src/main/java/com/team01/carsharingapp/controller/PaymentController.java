package com.team01.carsharingapp.controller;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.service.PaymentService;
import com.team01.carsharingapp.service.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints for managing payments")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/payment")
public class PaymentController {
    private static final String PAYMENT_SUCCESS = "Payment successful!";
    private static final String PAYMENT_CANCEL = "Payment canceled!";
    private static final String PAYMENT_ERROR = "Problem with session id, "
            + "can't approve your payment!";
    private static final String PAYMENT_FAILED = "Payment failed!";

    private final PaymentService paymentService;
    private final StripeService stripeService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping
    @Operation(summary = "Get all payments",
            description = "Get list of user payments. Only for manager/admin")
    public List<PaymentDto> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping
    @Operation(summary = "Get all payments by user ID", description = "Get list of user payments.")
    public List<PaymentDto> getPayments(@RequestParam("user_id") Long userId) {
        return paymentService.getPaymentsByUserId(userId);
    }

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Create a new payment. "
            + "Validation included.")
    public PaymentDto createPayment(
            @RequestBody @Valid PaymentRequestDto requestDto) {
        return paymentService.createPayment(requestDto);
    }

    @GetMapping("/success")
    @Operation(summary = "Gets a successful payments from Stripe")
    public String checkSuccessfulPayments(@RequestParam String sessionId) {
        if (!stripeService.isPaid(sessionId)) {
            return PAYMENT_ERROR;
        }
        return paymentService.setPaymentSuccessStatus(sessionId)
                ? PAYMENT_SUCCESS : PAYMENT_FAILED;
    }

    @GetMapping("/cancel")
    @Operation(summary = "Gets a canceled payments from Stripe")
    public String returnPaymentPausedMessage() {
        return PAYMENT_CANCEL;
    }
}
