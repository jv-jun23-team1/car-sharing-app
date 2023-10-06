package com.coffee.jedi.carsharingapp.service;

import com.coffee.jedi.carsharingapp.model.NotificationSubscription;

public interface NotificationService {
    void sendNotificationBySubscription(
            NotificationSubscription.SubscriptionType type,
            String string
    );
}
