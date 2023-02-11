package ru.zentsova.application.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "prescoring")
@Getter
@Setter
public class PrescoringProperties {

    private AgeProperties age;
    private TermProperties term;
    private AmountProperties amount;
    private RegExProp regex;

    @Getter
    @Setter
    public static class AgeProperties {
        private int min;
    }

    @Getter
    @Setter
    public static class TermProperties {
        private int min;
    }

    @Getter
    @Setter
    public static class AmountProperties {
        private BigDecimal requestedMin;
    }

    @Getter
    @Setter
    public static class RegExProp {
        private String name;
        private String email;
        private PassportRegExProp passport;

        @Setter
        @Getter
        public static class PassportRegExProp {
            private String series;
            private String number;
        }
    }
}