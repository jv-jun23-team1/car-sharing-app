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
    private static final String HEADER =
            NotificationSubscription.SubscriptionType.DAILY_OVERDUE.getText() + ": ";
    private static final String NO_OVERDUE_MESSAGE =
            HEADER + "Сьогодні немає простроченої орендної плати!";
    private static final String OVERDUE_MESSAGE =
            "Клієнт - %s, очікувана дата повернення - %s, машина - %s";
    private static final String LONG_HEADER = HEADER + " звіт по просроченій оренді на ";
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
        String actualHeader = LONG_HEADER + LocalDate.now() + System.lineSeparator();
        return actualHeader + overdue.stream()
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
