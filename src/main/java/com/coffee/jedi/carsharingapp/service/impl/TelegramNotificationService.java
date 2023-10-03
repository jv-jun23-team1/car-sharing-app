package com.coffee.jedi.carsharingapp.service.impl;

import com.coffee.jedi.carsharingapp.service.NotificationService;
import com.coffee.jedi.carsharingapp.telegramapi.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final Bot bot;

    @Override
    public void sendNotification(String string) {
        bot.sendMessage(string);
    }
}
