package com.team01.carsharingapp.telegramapi;

import com.team01.carsharingapp.exception.TelegramException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    private final String botUsername;
    private final Long chatId;

    public Bot(String botUsername, String botToken, Long chatId) {
        super(botToken);
        this.botUsername = botUsername;
        this.chatId = chatId;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().getText().equals("/start")) {
            sendMessage("Hello mate");
        } else {
            sendMessage("Unknown command");
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public void sendMessage(String message) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .build());
        } catch (TelegramApiException e) {
            throw new TelegramException("Can't execute SendMessage for message = " + message);
        }
    }
}

