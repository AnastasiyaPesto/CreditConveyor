package ru.zentsova.Dossier.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public class KafkaProperties {

    private String bootstrapServers;
    private Topic finishRegistrationTopic = new Topic();
    private Topic createDocumentsTopic = new Topic();
    private Topic sendDocumentsTopic = new Topic();
    private Topic sendSesTopic = new Topic();
    private Topic creditIssuedTopic = new Topic();
    private Topic applicationDeniedTopic = new Topic();

    @Getter
    @Setter
    public static class Topic {
        private String name;
    }
}
