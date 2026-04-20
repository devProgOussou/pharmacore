package com.pharmacore.notification.application.service;

import com.pharmacore.notification.domain.model.Notification;
import com.pharmacore.notification.domain.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

class NotificationApplicationServiceTest {

    private NotificationApplicationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationApplicationService();
    }

    @Test
    @DisplayName("publish() doit émettre la notification sur le stream")
    void shouldEmitNotificationOnStream() {
        Notification notification = Notification.of(
                NotificationType.LOW_STOCK,
                "Stock faible : Paracétamol",
                "{\"drugId\":\"123\",\"currentStock\":5}"
        );

        /*
         * StepVerifier — l'outil de test réactif de Project Reactor.
         * Il souscrit au Flux et vérifie les événements dans l'ordre.
         * Sans lui, le Flux ne s'exécute jamais (lazy evaluation).
         */
        Flux<Notification> stream = service.streamAll();

        StepVerifier.create(stream.take(1))
                .then(() -> service.publish(notification).subscribe())
                .expectNextMatches(n ->
                        n.type() == NotificationType.LOW_STOCK &&
                                n.message().equals("Stock faible : Paracétamol")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("streamByType() doit filtrer uniquement les notifications du type demandé")
    void shouldFilterByType() {
        Notification lowStock = Notification.of(
                NotificationType.LOW_STOCK, "Stock faible", null);
        Notification expiry = Notification.of(
                NotificationType.DRUG_EXPIRY, "Lot expiré", null);

        StepVerifier.create(service.streamByType(NotificationType.LOW_STOCK).take(1))
                .then(() -> {
                    service.publish(expiry).subscribe();
                    service.publish(lowStock).subscribe();
                })
                .expectNextMatches(n -> n.type() == NotificationType.LOW_STOCK)
                .verifyComplete();
    }

    @Test
    @DisplayName("publish() doit retourner la notification publiée")
    void shouldReturnPublishedNotification() {
        Notification notification = Notification.of(
                NotificationType.SYSTEM, "Démarrage du service", null);

        StepVerifier.create(service.publish(notification))
                .expectNextMatches(n ->
                        n.id() != null &&
                                n.type() == NotificationType.SYSTEM &&
                                n.occurredAt() != null
                )
                .verifyComplete();
    }
}
