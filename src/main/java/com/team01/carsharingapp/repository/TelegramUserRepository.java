package com.team01.carsharingapp.repository;

import com.team01.carsharingapp.model.NotificationSubscription;
import com.team01.carsharingapp.model.TelegramUser;
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
