package ru.zentsova.deal.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.kafka")
@Getter
@Setter
public class KafkaProperties {
    private String finishRegistrationTopic;
    private String createDocumentsTopic;
    private String sendDocumentsTopic;
    private String sendSesTopic;
    private String creditIssuedTopic;
    private String applicationDeniedTopic;

}
