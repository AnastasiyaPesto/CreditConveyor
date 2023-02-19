package ru.zentsova.application.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "prescoring")
@Setter
@Getter
public class PrescoringProperties {

    private AgeProperties age = new AgeProperties();
    private TermProperties term = new TermProperties();
    private AmountProperties amount = new AmountProperties();
    private RegExProp regex = new RegExProp();

    @Setter
    @Getter
    public static class AgeProperties {
        private int min;
    }

    @Setter
    @Getter
    public static class TermProperties {
        private int min;
    }

    @Setter
    @Getter
    public static class AmountProperties {
        private BigDecimal requestedMin;
    }

    @Setter
    @Getter
    public static class RegExProp {
        private String name;
        private String email;
        private PassportRegExProp passport = new PassportRegExProp();

        @Setter
        @Getter
        public static class PassportRegExProp {
            private String series;
            private String number;
        }
    }
}