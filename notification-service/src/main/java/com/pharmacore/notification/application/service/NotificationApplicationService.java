package com.pharmacore.notification.application.service;

import com.pharmacore.notification.domain.model.Notification;
import com.pharmacore.notification.domain.model.NotificationType;
import com.pharmacore.notification.domain.port.in.NotificationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public class NotificationApplicationService implements NotificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificationApplicationService.class);

    /*
     * Sinks.many().multicast() — un producer, N consumers.
     * replay(50)               — les 50 dernières notifications sont rejouées
     *                            pour tout nouveau subscriber qui se connecte.
     * onBackpressureBuffer()   — si un consumer est lent, on bufferise
     *                            plutôt que de dropper les événements.
     */
    private final Sinks.Many<Notification> sink = Sinks.many()
            .replay()
            .limit(50);

    @Override
    public Mono<Notification> publish(Notification notification) {
        return Mono.fromCallable(() -> {
            Sinks.EmitResult result = sink.tryEmitNext(notification);
            if (result.isFailure()) {
                log.warn("Failed to emit notification: {} — reason: {}", notification.id(), result);
            } else {
                log.info("[NOTIFICATION] Published: type={} | message={}",
                        notification.type(), notification.message());
            }
            return notification;
        });
    }

    @Override
    public Flux<Notification> streamAll() {
        return sink.asFlux()
                .doOnSubscribe(s -> log.debug("[SSE] New subscriber connected — streaming all notifications"))
                .doOnCancel(() -> log.debug("[SSE] Subscriber disconnected"));
    }

    @Override
    public Flux<Notification> streamByType(NotificationType type) {
        return streamAll()
                .filter(n -> n.type() == type)
                .doOnSubscribe(s -> log.debug("[SSE] New subscriber connected — filtering by type={}", type));
    }
}