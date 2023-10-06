package com.team01.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.team01.carsharingapp.dto.payment.PaymentDto;
import com.team01.carsharingapp.dto.payment.PaymentRequestDto;
import com.team01.carsharingapp.dto.stripe.StripeDto;
import com.team01.carsharingapp.exception.EntityNotFoundException;
import com.team01.carsharingapp.mapper.PaymentMapper;
import com.team01.carsharingapp.model.Car;
import com.team01.carsharingapp.model.Payment;
import com.team01.carsharingapp.model.Rental;
import com.team01.carsharingapp.model.User;
import com.team01.carsharingapp.repository.PaymentRepository;
import com.team01.carsharingapp.repository.RentalRepository;
import com.team01.carsharingapp.service.impl.PaymentServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {
    private static final Rental VALID_RENTAL = new Rental();
    private static final Payment VALID_PAYMENT = new Payment();

    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripeService stripeService;

    @BeforeAll
    static void setUp() {
        VALID_RENTAL.setId(1L);
        VALID_RENTAL.setUser(new User());
        VALID_RENTAL.getUser().setId(1L);
        VALID_RENTAL.setCar(new Car());
        VALID_RENTAL.getCar().setDailyFee(BigDecimal.valueOf(20));
        VALID_RENTAL.setRentalDate(LocalDate.now());
        VALID_RENTAL.setReturnDate(LocalDate.now().plusDays(2));
    }

    @Test
    public void createPayment_ValidRequest_ReturnsPaymentDto() {
        StripeDto stripeDto = new StripeDto();
        stripeDto.setSessionUrl("http://example.com");
        stripeDto.setSessionId("session_id");
        PaymentDto expected = new PaymentDto();
        expected.setSessionId("session_id");
        expected.setSessionUrl("http://example.com");
        Long rentalId = 1L;
        String type = "PAYMENT";
        PaymentRequestDto requestDto = new PaymentRequestDto(rentalId, type);

        when(rentalRepository.findByIdWithFetch(anyLong())).thenReturn(Optional.of(VALID_RENTAL));
        when(paymentRepository.findAllByUserId(anyLong())).thenReturn(new ArrayList<>());
        when(paymentMapper.toEntity(requestDto)).thenReturn(VALID_PAYMENT);
        when(stripeService.pay(VALID_PAYMENT, "usd")).thenReturn(stripeDto);
        when(paymentRepository.save(VALID_PAYMENT)).thenReturn(VALID_PAYMENT);
        when(paymentMapper.toDto(VALID_PAYMENT)).thenReturn(expected);

        PaymentDto result = paymentService.createPayment(requestDto);

        assertEquals("http://example.com", result.getSessionUrl());
        assertEquals("session_id", result.getSessionId());
    }

    @Test
    public void createPayment_NullRequest_ThrowsNullPointerException() {
        PaymentRequestDto requestDto = null;
        assertThrows(NullPointerException.class,
                () -> paymentService.createPayment(requestDto));
    }

    @Test
    public void createPayment_InvalidRentalId_ThrowsEntityNotFoundException() {
        Long rentalId = 1L;
        String type = "PAYMENT";
        PaymentRequestDto requestDto = new PaymentRequestDto(rentalId, type);

        when(rentalRepository.findByIdWithFetch(rentalId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> paymentService.createPayment(requestDto));
    }

    @Test
    public void createPayment_StripeServiceFails_ThrowsNullPointerException() {
        Long rentalId = 1L;
        String type = "PAYMENT";
        PaymentRequestDto requestDto = new PaymentRequestDto(rentalId, type);

        when(rentalRepository.findByIdWithFetch(rentalId)).thenReturn(Optional.of(VALID_RENTAL));
        when(paymentRepository.findAllByUserId(anyLong())).thenReturn(new ArrayList<>());
        when(paymentMapper.toEntity(requestDto)).thenReturn(VALID_PAYMENT);
        when(stripeService.pay(VALID_PAYMENT, "usd")).thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> paymentService.createPayment(requestDto));
    }

    @Test
    public void createPayment_NullSessionInfo_ThrowsNullPointerException() {
        Long rentalId = 1L;
        String type = "PAYMENT";
        PaymentRequestDto requestDto = new PaymentRequestDto(rentalId, type);

        when(rentalRepository.findByIdWithFetch(rentalId)).thenReturn(Optional.of(VALID_RENTAL));
        when(paymentRepository.findAllByUserId(anyLong())).thenReturn(new ArrayList<>());
        when(paymentMapper.toEntity(requestDto)).thenReturn(VALID_PAYMENT);
        when(stripeService.pay(VALID_PAYMENT, "usd")).thenReturn(new StripeDto());
        assertThrows(NullPointerException.class,
                () -> paymentService.createPayment(requestDto));
    }
}
