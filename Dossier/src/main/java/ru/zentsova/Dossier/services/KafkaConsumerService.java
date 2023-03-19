package ru.zentsova.Dossier.services;

import ru.zentsova.Dossier.dto.EmailMessage;

public interface KafkaConsumerService {

    void sendFinishRegistrationEvent(EmailMessage msg);
}