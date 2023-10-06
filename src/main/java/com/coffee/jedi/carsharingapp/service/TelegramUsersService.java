package com.coffee.jedi.carsharingapp.service;

import com.coffee.jedi.carsharingapp.model.NotificationSubscription;
import com.coffee.jedi.carsharingapp.model.TelegramUser;
import java.util.List;

public interface TelegramUsersService {
    List<TelegramUser> findBySubscriptionType(NotificationSubscription.SubscriptionType type);

    TelegramUser findByChatId(Long id);

    void save(TelegramUser user);
}
