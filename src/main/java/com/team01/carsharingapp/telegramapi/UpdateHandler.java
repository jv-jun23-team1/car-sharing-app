package com.team01.carsharingapp.telegramapi;

import com.team01.carsharingapp.event.TelegramMethodEvent;
import com.team01.carsharingapp.exception.TelegramException;
import com.team01.carsharingapp.model.NotificationSubscription;
import com.team01.carsharingapp.model.TelegramUser;
import com.team01.carsharingapp.repository.NotificationSubscriptionRepository;
import com.team01.carsharingapp.service.TelegramUsersService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
@RequiredArgsConstructor
public class UpdateHandler {
    private static final String UNSUPPORTED_USER_MESSAGE = """
            Ви не маєте доступу до функцій цього бота.
            Щоб отримати доступ, зверніться до адміністратора.
            Після отримання доступу викличіть команду /start
            """;
    private static final String HELLO_TEXT = """
            Вітаю, вам доступний функціонал по отриманню нотифікацій
            
            Наразі у вас не активована підписка ні на один з івентів.
            Для підписання натисніть відповідну кнопку, після чого
            поряд з назвою з'явиться "✔️" що означає що підписка активована.
            Для того щоб відписатися - натисніть повторно.
            Доступні підписки:
             "Просрочка" - кожного дня о 12:00 сповіщення про просрочені оренди
             "Оренда" - сповіщення про створення нової оренди
             "Оплата" - сповіщення про оплату
             
            Приємного користування
            """;
    private static final String UNSUPPORTED_TEXT = """
            Дана команда не підтримується ботом
            """;
    private static final String SUBSCRIBE_TEXT = "Ви підписались на \"";
    private static final String UNSUBSCRIBE_TEXT = "Ви відмінили підписку на \"";

    private final TelegramUsersService telegramUsersService;
    private final NotificationSubscriptionRepository notificationSubscriptionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void handle(Update update) {
        SendMessage message = null;
        TelegramUser user = null;
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            try {
                user = telegramUsersService.findByChatId(chatId);
            } catch (TelegramException e) {
                message = buildMessageUnsupportedUser(update.getMessage());
            }
            if (user != null) {
                message = messageHandler(update.getMessage(), user);
            }
        } else {
            throw new TelegramException("Unsupported type update = " + update);
        }
        applicationEventPublisher.publishEvent(TelegramMethodEvent.of(this, message));
    }

    private SendMessage messageHandler(Message message, TelegramUser user) {
        String text = message.getText();
        return switch (text) {
            case "/start" -> buildMessageHello(message);
            case "Просрочка", "Оренда", "Оплата" -> addSubscription(text, user);
            case "Просрочка ✔️", "Оренда ✔️", "Оплата ✔️" -> removeSubscription(text, user);
            default -> buildUnsupportedMessage(message);
        };

    }

    private SendMessage addSubscription(String text, TelegramUser user) {
        NotificationSubscription sub = notificationSubscriptionRepository.findByType(
                NotificationSubscription.SubscriptionType.findByText(text)
        );
        user.getSubscriptions().add(sub);
        telegramUsersService.save(user);
        List<NotificationSubscription.SubscriptionType> types = user.getSubscriptions().stream()
                .map(NotificationSubscription::getType)
                .toList();
        return SendMessage.builder()
                .chatId(user.getChatId())
                .replyMarkup(replySubscriptionKeyboard(types))
                .text(SUBSCRIBE_TEXT + text + "\"")
                .build();
    }

    private SendMessage removeSubscription(String text, TelegramUser user) {
        String typeText = text.replaceAll("(Просрочка|Оренда|Оплата).*", "$1");
        NotificationSubscription.SubscriptionType type =
                NotificationSubscription.SubscriptionType.findByText(typeText);
        List<NotificationSubscription> listSubs = new ArrayList<>(user.getSubscriptions());
        listSubs.removeIf(s -> s.getType().equals(type));
        user.setSubscriptions(listSubs);
        telegramUsersService.save(user);
        List<NotificationSubscription.SubscriptionType> filtered = user.getSubscriptions().stream()
                .map(NotificationSubscription::getType)
                .toList();
        return SendMessage.builder()
                .chatId(user.getChatId())
                .replyMarkup(replySubscriptionKeyboard(filtered))
                .text(UNSUBSCRIBE_TEXT + typeText + "\"")
                .build();
    }

    private SendMessage buildUnsupportedMessage(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(UNSUPPORTED_TEXT)
                .build();
    }

    private SendMessage buildMessageHello(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .replyMarkup(replySubscriptionKeyboard(List.of()))
                .text(HELLO_TEXT)
                .build();
    }

    private SendMessage buildMessageUnsupportedUser(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(UNSUPPORTED_USER_MESSAGE)
                .build();
    }

    private ReplyKeyboardMarkup replySubscriptionKeyboard(
            final List<NotificationSubscription.SubscriptionType> subscriptions
    ) {
        Function<NotificationSubscription.SubscriptionType, String> subsMapper = sub -> {
            if (subscriptions.contains(sub)) {
                return sub.getText() + " ✔️";
            }
            return sub.getText();
        };
        List<String> buttonsText = Arrays.stream(NotificationSubscription.SubscriptionType.values())
                .map(subsMapper)
                .toList();
        KeyboardRow keyboardRow = new KeyboardRow();
        buttonsText.forEach(text -> keyboardRow.add(KeyboardButton.builder().text(text).build()));
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(keyboardRow))
                .resizeKeyboard(true)
                .build();
    }
}
