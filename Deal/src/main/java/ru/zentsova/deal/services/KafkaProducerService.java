package ru.zentsova.deal.services;

public interface KafkaProducerService {

    void sendFinishRegistrationEvent(Long applicationId);
}