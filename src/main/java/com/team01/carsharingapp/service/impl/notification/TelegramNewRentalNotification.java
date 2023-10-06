package com.team01.carsharingapp.service.impl.notification;

import com.team01.carsharingapp.event.CreateRentalEvent;
import com.team01.carsharingapp.model.NotificationSubscription;
import com.team01.carsharingapp.model.Rental;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramNewRentalNotification {
    private static final String CREATE_RENTAL_MESSAGE =
            "NEW RENTAL : Customer - %s, expected return date - %s, car - %s";
    private final TelegramNotificationService telegramNotificationService;

    @EventListener
    private void sendCreatedRental(CreateRentalEvent createRentalEvent) {
        String message = buildCreatedRentalMessage(createRentalEvent.getRental());
        NotificationSubscription.SubscriptionType type =
                NotificationSubscription.SubscriptionType.NEW_RENT;
        telegramNotificationService.sendNotificationBySubscription(type, message);
    }

    private String buildCreatedRentalMessage(Rental rental) {
        String email = rental.getUser().getEmail();
        String returnDate = rental.getReturnDate().toString();
        String car = rental.getCar().getBrand() + " " + rental.getCar().getModel();
        return String.format(CREATE_RENTAL_MESSAGE, email, returnDate, car);
    }

}
