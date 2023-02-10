package ru.zentsova.application.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "regex")
public class RegExConfigProperties {
    private String name;
    private String email;
    private PassportRegEx passport;
}
