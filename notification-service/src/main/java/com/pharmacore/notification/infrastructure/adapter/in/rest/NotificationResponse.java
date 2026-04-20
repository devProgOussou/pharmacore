package com.pharmacore.notification.infrastructure.adapter.in.rest;

import com.pharmacore.notification.domain.model.Notification;
import com.pharmacore.notification.domain.model.NotificationType;

import java.time.Instant;

public record NotificationResponse(
        String id,
        NotificationType type,
        String message,
        String payload,
        Instant occurredAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.id(), n.type(), n.message(), n.payload(), n.occurredAt()
        );
    }
}
