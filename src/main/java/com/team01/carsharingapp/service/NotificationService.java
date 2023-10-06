package com.team01.carsharingapp.service;

import com.team01.carsharingapp.model.NotificationSubscription;

public interface NotificationService {
    void sendNotificationBySubscription(
            NotificationSubscription.SubscriptionType type,
            String string
    );
}
