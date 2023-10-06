package com.coffee.jedi.carsharingapp.event;

import java.io.Serializable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@Getter
public class TelegramMethodEvent extends ApplicationEvent {
    private final BotApiMethod<? extends Serializable> method;

    public TelegramMethodEvent(Object source, BotApiMethod<? extends Serializable> method) {
        super(source);
        this.method = method;
    }

    public static TelegramMethodEvent of(
            Object source,
            BotApiMethod<? extends Serializable> method
    ) {
        return new TelegramMethodEvent(source, method);
    }
}
