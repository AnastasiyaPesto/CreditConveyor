package ru.zentsova.deal.services;

public interface KafkaProducerService {

    void sendFinishRegistrationEvent(Long applicationId);

    void sendCreateDocumentsEvent(Long applicationId);

    void sendSendDocumentsEvent(Long applicationId);

    void sendSendSesEvent(Long applicationId);

    void sendCreditIssuedEvent(Long applicationId);

    void sendApplicationDeniedEvent(Long applicationId);

    void sendEventToKafka(String topic, String event);

}