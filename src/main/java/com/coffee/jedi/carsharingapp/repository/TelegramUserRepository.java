package com.coffee.jedi.carsharingapp.repository;

import com.coffee.jedi.carsharingapp.model.NotificationSubscription;
import com.coffee.jedi.carsharingapp.model.TelegramUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    @Query("SELECT u FROM TelegramUser u JOIN u.subscriptions s WHERE s.type = :subscriptionType")
    List<TelegramUser> findBySubscriptionType(
            NotificationSubscription.SubscriptionType subscriptionType
    );

    Optional<TelegramUser> findByChatId(Long chatId);
}
