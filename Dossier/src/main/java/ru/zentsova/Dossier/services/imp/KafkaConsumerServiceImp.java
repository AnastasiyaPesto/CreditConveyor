package ru.zentsova.Dossier.services.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.zentsova.Dossier.config.properties.EmailMessagesProperties;
import ru.zentsova.Dossier.dto.EmailMessageDto;
import ru.zentsova.Dossier.model.EmailMsgTemplate;
import ru.zentsova.Dossier.services.EmailService;
import ru.zentsova.Dossier.services.KafkaConsumerService;
import ru.zentsova.Dossier.utils.MessageCode;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerServiceImp implements KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final MailProperties mailProperties;
    private final MessageService messageService;
    private final EmailMessagesProperties messagesProperties;

     @KafkaListener(topics = { "finish-registration" }, groupId = "deal")
    public void finishRegistrationEvent(String event) {
        log.info("Event <<< {}", event);
        final Optional<EmailMessageDto> msgReceived = convertToEmailMessageDto(event);
        if (msgReceived.isPresent()) {
            EmailMsgTemplate msgTemplate = createEmailMsgTemplate(msgReceived.get());
            emailService.sendSimpleMessage(msgTemplate);
        }
    }

    @KafkaListener(topics = { "create-documents" }, groupId = "deal" )
    public void createDocumentsEvent(String event) {
        log.info("Event <<< {}", event);
        final Optional<EmailMessageDto> msgReceived = convertToEmailMessageDto(event);
        if (msgReceived.isPresent()) {
            EmailMsgTemplate msgTemplate = createEmailMsgTemplate(msgReceived.get());
            emailService.sendMimeMessage(msgTemplate, true);
        }
    }

    @KafkaListener(topics = { "send-documents" }, groupId = "deal" )
    public void sendDocumentsEvent(String event) {

    }

    @KafkaListener(topics = { "send-ses" }, groupId = "deal" )
    public void sendDSesEvent(String event) {

    }

    @KafkaListener(topics = { "credit-issued" }, groupId = "deal" )
    public void creditIssuedEvent(String event) {

    }

    @KafkaListener(topics = { "application-denied" }, groupId = "deal" )
    public void applicationDeniedEvent(String event) {

    }

    private EmailMsgTemplate createEmailMsgTemplate(EmailMessageDto messageDto) {
         return EmailMsgTemplate.builder()
                    .from(mailProperties.getUsername())
                    .to(messageDto.getAddress())
                    .subject(messageDto.getTheme().getValue())
                    .body(messageService.getMessage(MessageCode.EMAIL_MSG_FINISH_REGISTRATION))
                    .build();
    }

    private Optional<EmailMessageDto> convertToEmailMessageDto(String event) {
        try {
            return Optional.of(objectMapper.readValue(event, EmailMessageDto.class));
        } catch (JsonProcessingException ex) {
            log.info("Bad message to deserialize {}", event);
            ex.printStackTrace();
        }

        return Optional.empty();
    }
}