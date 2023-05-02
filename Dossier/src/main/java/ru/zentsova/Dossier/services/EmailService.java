package ru.zentsova.Dossier.services;

import org.springframework.mail.MailMessage;

public interface EmailService {

    void sendMessage(String from, String to, String subject, String body);

    MailMessage createMessage(String from, String to, String subject, String body);
}