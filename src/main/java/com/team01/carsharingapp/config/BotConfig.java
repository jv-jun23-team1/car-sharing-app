package com.team01.carsharingapp.config;

import com.team01.carsharingapp.telegramapi.Bot;
import com.team01.carsharingapp.telegramapi.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Value(value = "${bot.name}")
    private String botName;
    @Value(value = "${bot.token}")
    private String token;

    @Bean
    public Bot bot(@Autowired UpdateHandler updateHandler) {
        return new Bot(botName, token, updateHandler);
    }
}
