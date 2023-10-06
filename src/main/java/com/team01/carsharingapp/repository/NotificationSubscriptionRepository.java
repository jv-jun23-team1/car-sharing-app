package com.team01.carsharingapp.repository;

import com.team01.carsharingapp.model.NotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSubscriptionRepository
        extends JpaRepository<NotificationSubscription, Long> {
    NotificationSubscription findByType(NotificationSubscription.SubscriptionType type);
}
