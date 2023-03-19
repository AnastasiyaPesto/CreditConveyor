package ru.zentsova.deal.config.properties;

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
    private Topic topic = new Topic();

    @Getter
    @Setter
    public static class Topic {
        private String finishRegistration;
        private String createDocuments;
        private String sendDocuments;
        private String sendSes;
        private String creditIssued;
        private String applicationDenied;
    }
}
