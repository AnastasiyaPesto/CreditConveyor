package ru.zentsova.deal.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.zentsova.deal.config.properties.KafkaProperties;
import ru.zentsova.deal.dto.EmailMessageDto;
import ru.zentsova.deal.dto.Theme;
import ru.zentsova.deal.model.Application;
import ru.zentsova.deal.repositories.ApplicationRepository;
import ru.zentsova.deal.repositories.ClientRepository;
import ru.zentsova.deal.services.KafkaProducerService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImp implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaProperties kafkaProperties;

    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;

    public void sendFinishRegistrationEvent(Long applicationId) {
        EmailMessageDto msg = createMessage(applicationId);
        String msgToSend = "";
        try {
            msgToSend = objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException ex) {
            log.error("Bad message to serialize {}", msg);
            ex.printStackTrace();
        }
        String topicName = kafkaProperties.getTopic().getFinishRegistration();
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, msgToSend);
        kafkaTemplate.send(record);
        log.info("Event >>> {}", msgToSend);
    }

    private EmailMessageDto createMessage(Long applicationId) {
        final Optional<Application> application = applicationRepository.findById(applicationId);
        final EmailMessageDto msg = new EmailMessageDto();
        msg.setApplicationId(applicationId);
        application.ifPresent(value -> msg.setAddress(value.getClient().getEmail()));
        msg.setTheme(Theme.FINISH_REGISTRATION);
        return msg;
    }
}