package com.pharmacore.drug.infrastructure.config;

import com.pharmacore.drug.application.service.DrugApplicationService;
import com.pharmacore.drug.domain.port.out.DrugRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public DrugApplicationService drugApplicationService(DrugRepositoryPort drugRepositoryPort) {
        return new DrugApplicationService(drugRepositoryPort);
    }
}
