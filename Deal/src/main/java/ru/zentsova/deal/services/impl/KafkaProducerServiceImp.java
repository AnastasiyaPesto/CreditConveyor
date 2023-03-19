package ru.zentsova.deal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.zentsova.deal.config.properties.KafkaProperties;
import ru.zentsova.deal.dto.EmailMessage;
import ru.zentsova.deal.dto.Theme;
import ru.zentsova.deal.services.KafkaProducerService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImp implements KafkaProducerService {

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void sendFinishRegistrationEvent(Long applicationId) {
        EmailMessage msg = createEmailMsg(applicationId);
        ProducerRecord<String, EmailMessage> record = new ProducerRecord<>(kafkaProperties.getTopic().getFinishRegistration(), msg);
        kafkaTemplate.send(record);
    }

    private EmailMessage createEmailMsg(Long applicationId) {
        final EmailMessage msg = new EmailMessage();
        msg.setApplicationId(applicationId);
        msg.setAddress("test@test.ru");
        msg.setTheme(Theme.FINISH_REGISTRATION);
        return msg;
    }
}