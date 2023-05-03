package ru.zentsova.Dossier.services;

import org.springframework.mail.MailMessage;
import ru.zentsova.Dossier.model.EmailMsgTemplate;

public interface EmailService {

    void sendSimpleMessage(EmailMsgTemplate emailMsgTemplate);

    void sendMimeMessage(EmailMsgTemplate emailMsgTemplate, boolean html);

}