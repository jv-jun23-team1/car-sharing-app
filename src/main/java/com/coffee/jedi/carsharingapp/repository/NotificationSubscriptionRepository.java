package com.coffee.jedi.carsharingapp.repository;

import com.coffee.jedi.carsharingapp.model.NotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSubscriptionRepository
        extends JpaRepository<NotificationSubscription, Long> {
    NotificationSubscription findByType(NotificationSubscription.SubscriptionType type);
}
