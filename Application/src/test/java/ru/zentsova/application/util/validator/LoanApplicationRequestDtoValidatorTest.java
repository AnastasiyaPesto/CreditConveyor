package ru.zentsova.application.util.validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.zentsova.application.config.properties.PrescoringProperties;
import ru.zentsova.application.exceptions.ApplicationException;
import ru.zentsova.application.model.LoanApplicationRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationRequestDtoValidatorTest {

    private static LoanApplicationRequestDtoValidator validator;

    @BeforeAll
    public static void init() {
        PrescoringProperties.AgeProperties ageProp = new PrescoringProperties.AgeProperties();
        ageProp.setMin(21);

        PrescoringProperties.AmountProperties amountProp = new PrescoringProperties.AmountProperties();
        amountProp.setRequestedMin(new BigDecimal("10000.00"));

        PrescoringProperties.TermProperties termProp = new PrescoringProperties.TermProperties();
        termProp.setMin(6);

        PrescoringProperties.RegExProp regExProp = new PrescoringProperties.RegExProp();
        regExProp.setEmail("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        regExProp.setName("([a-zA-Z]{2,30})");
        PrescoringProperties.RegExProp.PassportRegExProp passportRegExProp = new PrescoringProperties.RegExProp.PassportRegExProp();
        passportRegExProp.setSeries("\\d{4}");
        passportRegExProp.setNumber("\\d{6}");

        PrescoringProperties prescoreProps = new PrescoringProperties();
        prescoreProps.setAge(ageProp);
        prescoreProps.setAmount(amountProp);
        prescoreProps.setTerm(termProp);
        regExProp.setPassport(passportRegExProp);
        prescoreProps.setRegex(regExProp);
        validator = new LoanApplicationRequestDtoValidator(prescoreProps);
    }

    @Test
    public void testValidate_shouldExceptionThrown_whenAllDtoParamsIsEmptyButShouldBeFilled() {
        LoanApplicationRequestDto dto = new LoanApplicationRequestDto();

        Exception ex = assertThrows(ApplicationException.class, () -> {
            validator.validate(dto, true);
        });

        String expectedMessage = "amount - Should not be empty; firstName - Should not be empty; lastName - Should not be empty; middleName - Should not be empty; " +
                "term - Should not be empty; birthdate - Should not be empty; email - Should not be empty; passportSeries - Should not be empty; " +
                "passportNumber - Should not be empty; ";
        String actualMessage = ex.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testValidate_shouldExceptionThrown_whenAllDtoParamsIsNotValid() {
        LoanApplicationRequestDto dto = createLoanApplicationRequestDto(new BigDecimal("9000.00"), 3, "a", "a", "a", "test@", LocalDate.of(2020, 1, 1), "1", "1");

        Exception ex = assertThrows(ApplicationException.class, () -> {
            validator.validate(dto, true);
        });

        String expectedMessage = "amount - Should be more or equal than 10000.00; firstName - Must be more than 2 and less than 30 characters and looks like Ivan; " +
                "lastName - Must be more than 2 and less than 30 characters and looks like Ivanov; " +
                "middleName - Must be more than 2 and less than 30 characters and looks like Ivanovich; term - Should be more or equal than 6; " +
                "birthdate - Age should be more or equal than 21; email - Should be like test@test.com; passportSeries - Length must be 4 digits; " +
                "passportNumber - Length must be 6 digits; ";
        String actualMessage = ex.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testValidate_shouldReturnTrue_whenAllDtoParamsIsValid() {
        LoanApplicationRequestDto dto = createLoanApplicationRequestDto(new BigDecimal("300000.00"), 60, "Anastasiia", "Zentsova", "Olegovna",
                "username@domain.co.in", LocalDate.of(1992, 2, 4), "4444", "666666");

        assertTrue(validator.validate(dto, true));
    }

    private LoanApplicationRequestDto createLoanApplicationRequestDto(BigDecimal requestedAmount, int term, String firstName, String lastName, String middleName,
                                                                      String email, LocalDate birthdate, String passportSeries, String passportNumber)
    {
        return new LoanApplicationRequestDto().amount(requestedAmount).term(term).firstName(firstName).lastName(lastName).middleName(middleName).email(email)
                .birthdate(birthdate).passportSeries(passportSeries).passportNumber(passportNumber);
    }

}