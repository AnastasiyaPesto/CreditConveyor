package ru.zentsova.Dossier.config.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "email.messages")
@Getter
@Setter
public class EmailMessagesProperties {

    private String finishRegistrationMsg;
    private String createDocumentsMsg;

}
