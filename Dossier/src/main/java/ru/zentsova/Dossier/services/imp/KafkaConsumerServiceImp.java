package ru.zentsova.Dossier.services.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.zentsova.Dossier.dto.EmailMessageDto;
import ru.zentsova.Dossier.services.KafkaConsumerService;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerServiceImp implements KafkaConsumerService {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = { "finish-registration" }, groupId = "deal")
    public void sendFinishRegistrationEvent(String msg) {
        Optional<EmailMessageDto> msgReceived = Optional.empty();
        try {
            msgReceived = Optional.of(objectMapper.readValue(msg, EmailMessageDto.class));
        } catch (JsonProcessingException ex) {
            log.info("Bad message to deserialize {}", msg);
            ex.printStackTrace();
        }
        if (msgReceived.isPresent()) {
            EmailMessageDto event = msgReceived.get();
            log.info("Event <<< {}", event);
        }
    }
}