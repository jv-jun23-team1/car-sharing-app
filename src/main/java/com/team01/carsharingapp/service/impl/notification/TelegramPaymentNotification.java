package com.team01.carsharingapp.service.impl.notification;

import com.team01.carsharingapp.event.PaymentEvent;
import com.team01.carsharingapp.model.NotificationSubscription;
import com.team01.carsharingapp.model.Payment;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramPaymentNotification {
    private static final String HEADER =
            NotificationSubscription.SubscriptionType.PAYMENT_COMPLETE.getText() + ": ";
    private static final String CREATE_RENTAL_MESSAGE = HEADER + """
            Клієнт - %s,
            Сума - %.2f
            Тип оплати - %s,
            Оренда :
              Машина - %s
              Очікувана дата повернення - %s,
              Фактична дата повернення - %s
            """;
    private final TelegramNotificationService telegramNotificationService;

    @EventListener
    private void sendCreatedRental(PaymentEvent paymentEvent) {
        Payment payment = paymentEvent.getPayment();
        String message = buildPaymentMessage(payment);
        NotificationSubscription.SubscriptionType type =
                NotificationSubscription.SubscriptionType.PAYMENT_COMPLETE;
        telegramNotificationService.sendNotificationBySubscription(type, message);
    }

    private String buildPaymentMessage(Payment payment) {
        String email = payment.getRental().getUser().getEmail();
        String returnDate = payment.getRental().getReturnDate().toString();
        String actualReturnDate = payment.getRental().getActualReturnDate().toString();
        BigDecimal bigDecimal = payment.getPrice();
        String paymentType = payment.getType().name();
        String car = payment.getRental().getCar().getBrand() + " "
                + payment.getRental().getCar().getModel();
        return String.format(
                CREATE_RENTAL_MESSAGE,
                email,
                bigDecimal,
                paymentType,
                car,
                returnDate,
                actualReturnDate);
    }
}
