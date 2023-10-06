package com.team01.carsharingapp.service;

import com.team01.carsharingapp.model.NotificationSubscription;
import com.team01.carsharingapp.model.TelegramUser;
import java.util.List;

public interface TelegramUsersService {
    List<TelegramUser> findBySubscriptionType(NotificationSubscription.SubscriptionType type);

    TelegramUser findByChatId(Long id);

    void save(TelegramUser user);
}
