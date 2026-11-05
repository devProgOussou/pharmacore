package com.pharmacore.drug.infrastructure.adapter.out.kafka;

import com.pharmacore.drug.domain.event.StockAlertEvent;
import com.pharmacore.drug.domain.port.out.DrugEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class DrugKafkaProducerAdapter implements DrugEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(DrugKafkaProducerAdapter.class);
    private static final String STOCK_ALERT_TOPIC = "stock.alert";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DrugKafkaProducerAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishStockAlert(StockAlertEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(STOCK_ALERT_TOPIC, event.drugId(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[KAFKA] Failed to publish StockAlertEvent for drug={} : {}",
                        event.drugId(), ex.getMessage());
            } else {
                log.info("[KAFKA] Published StockAlertEvent → topic={} partition={} offset={}",
                        STOCK_ALERT_TOPIC,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
