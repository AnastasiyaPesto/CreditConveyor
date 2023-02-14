package ru.zentsova.application.config.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PrescoringProperties.class)
@TestPropertySource(value = "classpath:application-test.properties")
public class PrescoringPropertiesTest {

    @Autowired
    private PrescoringProperties properties;

    @Test
    public void testProperties() {

        String emailRegEx = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        String nameRegEx = "([a-zA-Z]{2,30})";
        String passportSeriesRegEx = "\\d{4}";
        String passportNumberRegEx = "\\d{6}";

        assertEquals(30, properties.getAge().getMin());
        assertEquals(10, properties.getTerm().getMin());
        assertEquals(new BigDecimal("120.00"), properties.getAmount().getRequestedMin());
        assertEquals(emailRegEx, properties.getRegex().getEmail());
        assertEquals(nameRegEx, properties.getRegex().getName());
        assertEquals(passportSeriesRegEx, properties.getRegex().getPassport().getSeries());
        assertEquals(passportNumberRegEx, properties.getRegex().getPassport().getNumber());
    }

}