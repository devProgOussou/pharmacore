package com.pharmacore.notification.infrastructure.config;

import com.pharmacore.notification.application.service.NotificationApplicationService;
import com.pharmacore.notification.domain.port.in.NotificationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public NotificationUseCase notificationUseCase() {
        return new NotificationApplicationService();
    }
}
