package com.pharmacore.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Notification(
        String id,
        NotificationType type,
        String message,
        String payload,
        Instant occurredAt
) {
    public static Notification of(NotificationType type, String message, String payload) {
        return new Notification(
                UUID.randomUUID().toString(),
                type,
                message,
                payload,
                Instant.now()
        );
    }
}
