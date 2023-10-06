package com.team01.carsharingapp.service.impl;

import com.team01.carsharingapp.exception.TelegramException;
import com.team01.carsharingapp.model.NotificationSubscription;
import com.team01.carsharingapp.model.TelegramUser;
import com.team01.carsharingapp.repository.TelegramUserRepository;
import com.team01.carsharingapp.service.TelegramUsersService;
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
