package ru.zentsova.application.config.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PrescoringProperties.class)
@TestPropertySource(value = "classpath:application-test.properties")
public class PrescoringPropertiesTest {

    @Autowired
    private PrescoringProperties properties;

    @Test
    public void testProperties() {

        String emailRegEx = "^([\\w]+@[\\w]+.[a-zA-Z]+)$";
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

    @ParameterizedTest
    @MethodSource(value = "conditionsProvide")
    public void testEmailRegEx(String email, boolean result) {
        assertEquals(result, email.matches(properties.getRegex().getEmail()));
    }

    static Stream<Arguments> conditionsProvide() {
        return Stream.of(
            arguments("test@mail.ru", true),
            arguments("test1123@test123.rudsfsd", true),
            arguments("test1123@@test123.rudsfsd", false),
            arguments("test1123@test123.ru123", false)
        );
    }
}