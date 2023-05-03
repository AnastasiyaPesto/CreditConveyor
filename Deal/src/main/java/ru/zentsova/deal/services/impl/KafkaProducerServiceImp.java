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

    public void sendCreateDocumentsEvent(Long applicationId) {
        String msgToSend = createMessage(applicationId, Theme.CREATE_DOCUMENTS);
        sendEventToKafka(kafkaProperties.getCreateDocumentsTopic(), msgToSend);
    }

    public void sendFinishRegistrationEvent(Long applicationId) {
        String msgToSend = createMessage(applicationId, Theme.FINISH_REGISTRATION);
        sendEventToKafka(kafkaProperties.getFinishRegistrationTopic(), msgToSend);
    }


    @Override
    public void sendSendDocumentsEvent(Long applicationId) {

    }

    @Override
    public void sendSendSesEvent(Long applicationId) {

    }

    @Override
    public void sendCreditIssuedEvent(Long applicationId) {

    }

    @Override
    public void sendApplicationDeniedEvent(Long applicationId) {

    }

    private String createMessage(Long applicationId, Theme theme) {
        EmailMessageDto msg = createMessageDto(applicationId, theme);
        return convertEmailMessageDtoToString(msg);
    }

    private EmailMessageDto createMessageDto(Long applicationId, Theme theme) {
        final Optional<Application> application = applicationRepository.findById(applicationId);
        final EmailMessageDto msg = new EmailMessageDto();
        msg.setApplicationId(applicationId);
        application.ifPresent(value -> msg.setAddress(value.getClient().getEmail()));
        msg.setTheme(theme);

        return msg;
    }

    private String convertEmailMessageDtoToString(EmailMessageDto dto) {
        String msgToSend = "";
        try {
            msgToSend = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException ex) {
            log.error("Bad message to serialize {}", dto);
            ex.printStackTrace();
        }
        return msgToSend;
    }

    public void sendEventToKafka(String topic, String event) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, event);
        kafkaTemplate.send(record);
        log.info("Event >>> {}", event);
    }

}