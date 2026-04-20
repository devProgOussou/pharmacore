package com.pharmacore.notification.domain.port.in;

import com.pharmacore.notification.domain.model.Notification;
import com.pharmacore.notification.domain.model.NotificationType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationUseCase {

    // Publier une notification (appelé par Kafka en phase 3, par REST pour l'instant)
    Mono<Notification> publish(Notification notification);

    // Stream de toutes les notifications — connexion SSE permanente
    Flux<Notification> streamAll();

    // Stream filtré par type
    Flux<Notification> streamByType(NotificationType type);
}
