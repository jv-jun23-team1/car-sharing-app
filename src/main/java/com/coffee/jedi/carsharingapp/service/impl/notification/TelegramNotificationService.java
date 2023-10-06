package com.coffee.jedi.carsharingapp.service.impl.notification;

import com.coffee.jedi.carsharingapp.event.TelegramMethodEvent;
import com.coffee.jedi.carsharingapp.model.NotificationSubscription;
import com.coffee.jedi.carsharingapp.model.TelegramUser;
import com.coffee.jedi.carsharingapp.service.NotificationService;
import com.coffee.jedi.carsharingapp.service.TelegramUsersService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class TelegramNotificationService implements NotificationService {
    private final TelegramUsersService telegramUsersService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void sendNotificationBySubscription(
            NotificationSubscription.SubscriptionType type,
            String message
    ) {
        findChatIdBySubscriptionType(type).stream()
                .map(id -> buildEvent(id, message))
                .forEach(applicationEventPublisher::publishEvent);
    }

    public Set<Long> findChatIdBySubscriptionType(
            NotificationSubscription.SubscriptionType type
    ) {
        return telegramUsersService.findBySubscriptionType(type).stream()
                .map(TelegramUser::getChatId)
                .collect(Collectors.toSet());
    }

    private TelegramMethodEvent buildEvent(Long chatId, String message) {
        return TelegramMethodEvent.of(this,
                SendMessage.builder()
                        .chatId(chatId)
                        .text(message)
                        .build());
    }
}
