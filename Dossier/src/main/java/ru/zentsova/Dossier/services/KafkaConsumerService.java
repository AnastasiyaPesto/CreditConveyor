package ru.zentsova.Dossier.services;

public interface KafkaConsumerService {

    void sendFinishRegistrationEvent(String msg);
}