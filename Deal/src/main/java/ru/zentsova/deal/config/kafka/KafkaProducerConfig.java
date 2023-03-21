package ru.zentsova.deal.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.zentsova.deal.config.properties.KafkaProperties;
import ru.zentsova.deal.dto.EmailMessageDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    KafkaTemplate<String, EmailMessageDto> kafkaTemplate() {
        KafkaTemplate<String, EmailMessageDto> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setProducerListener(new ProducerListener<>() {
            public void onSuccess(ProducerRecord<String, EmailMessageDto> record, RecordMetadata metadata) {
                log.info("Event is sent: {}, topic {}, partition {}, offset {}, time {}", record.value(),
                        metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
            }

            public void onError(ProducerRecord<String, EmailMessageDto> record, RecordMetadata metadata, Exception ex) {
                log.error("error producing {}", ex.getMessage());
            }
        });
        return kafkaTemplate;
    }

    @Bean
    public ProducerFactory<String, EmailMessageDto> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }
}