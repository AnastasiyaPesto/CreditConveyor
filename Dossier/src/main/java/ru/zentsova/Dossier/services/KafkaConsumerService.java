package ru.zentsova.Dossier.services;

import ru.zentsova.Dossier.dto.EmailMessageDto;

public interface KafkaConsumerService {

    void sendFinishRegistrationEvent(EmailMessageDto msg);
}