package com.pharmacore.notification.infrastructure.adapter.in.rest;

import com.pharmacore.notification.domain.model.Notification;
import com.pharmacore.notification.domain.model.NotificationType;
import com.pharmacore.notification.domain.port.in.NotificationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationUseCase notificationUseCase;

    public NotificationController(NotificationUseCase notificationUseCase) {
        this.notificationUseCase = notificationUseCase;
    }

    /*
     * Endpoint pour publier une notification manuellement.
     * En phase 3, ce sera remplacé par un consumer Kafka.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<NotificationResponse> publish(@RequestBody NotificationRequest request) {
        Notification notification = Notification.of(
                request.type(),
                request.message(),
                request.payload()
        );
        return notificationUseCase.publish(notification)
                .map(NotificationResponse::from);
    }

    /*
     * SSE — Server-Sent Events.
     * MediaType.TEXT_EVENT_STREAM_VALUE est clé ici :
     * il dit au navigateur/client de garder la connexion ouverte
     * et de lire les événements au fur et à mesure qu'ils arrivent.
     *
     * ServerSentEvent<T> permet de structurer chaque événement
     * avec un id, un eventType, et des données.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationResponse>> streamAll() {
        return notificationUseCase.streamAll()
                .map(n -> ServerSentEvent.<NotificationResponse>builder()
                        .id(n.id())
                        .event(n.type().name())
                        .data(NotificationResponse.from(n))
                        .build())
                .mergeWith(heartbeat()); // keepalive toutes les 15s
    }

    @GetMapping(value = "/stream/{type}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationResponse>> streamByType(
            @PathVariable NotificationType type) {
        return notificationUseCase.streamByType(type)
                .map(n -> ServerSentEvent.<NotificationResponse>builder()
                        .id(n.id())
                        .event(n.type().name())
                        .data(NotificationResponse.from(n))
                        .build())
                .mergeWith(heartbeat());
    }

    /*
     * Heartbeat — événement vide envoyé toutes les 15s
     * pour garder la connexion SSE active et détecter
     * les clients déconnectés côté proxy/load balancer.
     */
    private Flux<ServerSentEvent<NotificationResponse>> heartbeat() {
        return Flux.interval(Duration.ofSeconds(15))
                .map(i -> ServerSentEvent.<NotificationResponse>builder()
                        .event("heartbeat")
                        .comment("keep-alive")
                        .build());
    }
}
