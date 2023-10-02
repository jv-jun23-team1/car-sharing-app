package com.team01.carsharingapp.config;

import com.team01.carsharingapp.exception.TelegramException;
import com.team01.carsharingapp.telegramapi.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@PropertySource("application.properties")
@Configuration
public class BotConfig {
    @Value(value = "${bot.name}") private String botName;
    @Value(value = "${bot.token}") private String token;
    @Value(value = "${bot.chatId}") private Long chatId;

    @Bean
    public Bot bot() {
        return new Bot(botName, token, chatId);
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot());
        } catch (TelegramApiException e) {
            throw new TelegramException("Can't initialize Bot");
        }
    }
}
