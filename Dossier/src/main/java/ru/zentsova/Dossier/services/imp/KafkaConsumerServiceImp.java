package ru.zentsova.Dossier.services.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.zentsova.Dossier.dto.EmailMessageDto;
import ru.zentsova.Dossier.services.KafkaConsumerService;


@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerServiceImp implements KafkaConsumerService {

    @KafkaListener(topics = { "finish-registration" }, groupId = "deal")
    public void sendFinishRegistrationEvent(EmailMessageDto msg) {
        log.info("Event is received: {}", msg);
    }
}