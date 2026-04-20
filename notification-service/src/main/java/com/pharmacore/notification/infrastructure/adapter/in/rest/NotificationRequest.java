package com.pharmacore.notification.infrastructure.adapter.in.rest;

import com.pharmacore.notification.domain.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @NotNull  NotificationType type,
        @NotBlank String message,
        String payload
) {}
