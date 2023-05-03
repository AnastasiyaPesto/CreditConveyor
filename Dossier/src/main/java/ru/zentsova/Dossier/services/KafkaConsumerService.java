package ru.zentsova.Dossier.services;

public interface KafkaConsumerService {

    void finishRegistrationEvent(String event);

    void createDocumentsEvent(String event);

    void sendDocumentsEvent(String event);

    void sendDSesEvent(String event);

    void creditIssuedEvent(String event);

    void applicationDeniedEvent(String event);
}