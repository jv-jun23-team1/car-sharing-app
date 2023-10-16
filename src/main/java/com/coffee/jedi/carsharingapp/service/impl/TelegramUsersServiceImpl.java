package com.coffee.jedi.carsharingapp.service.impl;

import com.coffee.jedi.carsharingapp.exception.TelegramException;
import com.coffee.jedi.carsharingapp.model.NotificationSubscription;
import com.coffee.jedi.carsharingapp.model.TelegramUser;
import com.coffee.jedi.carsharingapp.repository.TelegramUserRepository;
import com.coffee.jedi.carsharingapp.service.TelegramUsersService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramUsersServiceImpl implements TelegramUsersService {
    private final TelegramUserRepository telegramUserRepository;

    @Override
    public List<TelegramUser> findBySubscriptionType(
            NotificationSubscription.SubscriptionType type
    ) {
        return telegramUserRepository.findBySubscriptionType(type);
    }

    @Override
    public TelegramUser findByChatId(Long chatId) {
        return telegramUserRepository.findByChatId(chatId).orElseThrow(() ->
                new TelegramException("Not found by id = " + chatId));
    }

    @Override
    public void save(TelegramUser user) {
        telegramUserRepository.save(user);
    }
}
