package com.team01.carsharingapp.service.impl;

import com.team01.carsharingapp.dto.rental.CreateRentalEvent;
import com.team01.carsharingapp.model.Rental;
import com.team01.carsharingapp.repository.RentalRepository;
import com.team01.carsharingapp.service.NotificationService;
import com.team01.carsharingapp.telegramapi.Bot;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class TelegramNotificationService implements NotificationService {
    private static final String NO_OVERDUE_MESSAGE = "No rentals overdue today!";
    private static final String OVERDUE_MESSAGE =
            "Customer - %s, expected return date - %s, car - %s";
    private static final String CREATE_RENTAL_MESSAGE =
            "NEW RENTAL : Customer - %s, expected return date - %s, car - %s";
    private final Bot bot;
    private final RentalRepository rentalRepository;

    @Override
    public void sendNotification(String string) {
        bot.sendMessage(string);
    }

    @Scheduled(cron = "0 0 12 * * ?")
    private void sendDailyStatistic() {
        LocalDate date = LocalDate.now().plusDays(1);
        List<Rental> overdue = rentalRepository.findAllOverdue(date);
        String message = overdue.isEmpty() ? NO_OVERDUE_MESSAGE : buildOverdueMessage(overdue);
        sendNotification(message);
    }

    @EventListener
    private void sendCreatedRental(CreateRentalEvent createRentalEvent) {
        sendNotification(buildCreatedRentalMessage(createRentalEvent.getRental()));
    }

    private String buildCreatedRentalMessage(Rental rental) {
        String email = rental.getUser().getEmail();
        String returnDate = rental.getReturnDate().toString();
        String car = rental.getCar().getBrand() + " " + rental.getCar().getModel();
        return String.format(CREATE_RENTAL_MESSAGE, email, returnDate, car);
    }

    private String buildOverdueMessage(List<Rental> overdue) {
        Function<Rental, String> buildMessage = r -> {
            String email = r.getUser().getEmail();
            String returnDate = r.getReturnDate().toString();
            String car = r.getCar().getBrand() + " " + r.getCar().getModel();
            return String.format(OVERDUE_MESSAGE, email, returnDate, car);
        };
        Comparator<Rental> comparator = (f, s) -> {
            if (f.getReturnDate().isBefore(s.getReturnDate())) {
                return -1;
            }
            if (f.getReturnDate().isAfter(s.getReturnDate())) {
                return 1;
            }
            return 0;
        };
        return overdue.stream()
                .sorted(comparator)
                .map(buildMessage)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
