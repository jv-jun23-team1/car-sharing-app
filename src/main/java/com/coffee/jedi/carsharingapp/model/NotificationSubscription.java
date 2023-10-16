package com.coffee.jedi.carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Arrays;
import lombok.Data;

@Data
@Entity
@Table(name = "notification_subscription")
public class NotificationSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private NotificationSubscription.SubscriptionType type;

    public enum SubscriptionType {
        DAILY_OVERDUE("Просрочка"),
        NEW_RENT("Оренда"),
        PAYMENT_COMPLETE("Оплата");

        private String text;

        SubscriptionType(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static NotificationSubscription.SubscriptionType findByText(String text) {
            return Arrays.stream(SubscriptionType.values())
                    .filter(s -> s.getText().equals(text))
                    .findFirst().orElseThrow(() ->
                            new IllegalArgumentException("No type with text = " + text));
        }
    }
}
