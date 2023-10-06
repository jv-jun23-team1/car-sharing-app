package com.team01.carsharingapp.service.impl.notification;

import com.team01.carsharingapp.model.NotificationSubscription;
import com.team01.carsharingapp.model.Rental;
import com.team01.carsharingapp.repository.RentalRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramDailyOverdueNotification {
    private static final String NO_OVERDUE_MESSAGE = "No rentals overdue today!";
    private static final String OVERDUE_MESSAGE =
            "Customer - %s, expected return date - %s, car - %s";
    private final RentalRepository rentalRepository;
    private final TelegramNotificationService telegramNotificationService;

    @Scheduled(cron = "0 0 12 * * ?")
    public void sendDailyOverdueStatistic() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<Rental> overdue = rentalRepository.findAllOverdue(date);
        String message = overdue.isEmpty() ? NO_OVERDUE_MESSAGE : buildOverdueMessage(overdue);
        telegramNotificationService.sendNotificationBySubscription(
                NotificationSubscription.SubscriptionType.DAILY_OVERDUE,
                message
        );
    }

    private String buildOverdueMessage(List<Rental> overdue) {
        return overdue.stream()
                .sorted(rentalReturnDateComparator())
                .map(buildMessageFunction())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private Function<Rental, String> buildMessageFunction() {
        return rental -> {
            String email = rental.getUser().getEmail();
            String returnDate = rental.getReturnDate().toString();
            String car = rental.getCar().getBrand() + " " + rental.getCar().getModel();
            return String.format(OVERDUE_MESSAGE, email, returnDate, car);
        };
    }

    private Comparator<Rental> rentalReturnDateComparator() {
        return (first, second) -> {
            if (first.getReturnDate().isBefore(second.getReturnDate())) {
                return -1;
            }
            if (first.getReturnDate().isAfter(second.getReturnDate())) {
                return 1;
            }
            return 0;
        };
    }
}
