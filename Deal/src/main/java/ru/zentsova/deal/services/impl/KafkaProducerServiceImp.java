package ru.zentsova.deal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.zentsova.deal.config.properties.KafkaProperties;
import ru.zentsova.deal.dto.EmailMessageDto;
import ru.zentsova.deal.dto.Theme;
import ru.zentsova.deal.services.KafkaProducerService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImp implements KafkaProducerService {

    private final KafkaTemplate<String, EmailMessageDto> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void sendFinishRegistrationEvent(Long applicationId) {
        EmailMessageDto msg = createEmailMsg(applicationId);
        ProducerRecord<String, EmailMessageDto> record = new ProducerRecord<>(kafkaProperties.getTopic().getFinishRegistration(), msg);
        kafkaTemplate.send(record);
    }

    private EmailMessageDto createEmailMsg(Long applicationId) {
        final EmailMessageDto msg = new EmailMessageDto();
        msg.setApplicationId(applicationId);
        msg.setAddress("test@test.ru");
        msg.setTheme(Theme.FINISH_REGISTRATION);
        return msg;
    }
}