package com.coffee.jedi.carsharingapp.telegramapi;

import com.coffee.jedi.carsharingapp.event.TelegramMethodEvent;
import com.coffee.jedi.carsharingapp.exception.TelegramException;
import jakarta.annotation.PostConstruct;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Bot extends TelegramLongPollingBot {
    private final String botUsername;
    private final UpdateHandler updateHandler;

    public Bot(String botUsername, String botToken, UpdateHandler updateHandler) {
        super(botToken);
        this.botUsername = botUsername;
        this.updateHandler = updateHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateHandler.handle(update);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @EventListener
    private void complete(TelegramMethodEvent event) {
        try {
            execute(event.getMethod());
        } catch (Exception e) {
            throw new TelegramException("Can't execute method = " + event.getMethod()
                    + " source = " + event.getSource());
        }
    }

    @PostConstruct
    private void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new TelegramException("Can't initialize Bot");
        }
    }
}

