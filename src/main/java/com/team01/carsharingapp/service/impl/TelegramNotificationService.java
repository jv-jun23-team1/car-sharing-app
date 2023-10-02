package com.team01.carsharingapp.service.impl;

import com.team01.carsharingapp.service.NotificationService;
import com.team01.carsharingapp.telegramapi.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class    TelegramNotificationService implements NotificationService {
    private final Bot bot;

    @Override
    public void sendNotification(String string) {
        bot.sendMessage(string);
    }
}
