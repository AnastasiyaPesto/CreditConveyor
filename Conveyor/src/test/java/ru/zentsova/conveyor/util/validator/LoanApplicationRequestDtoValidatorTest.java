package ru.zentsova.conveyor.util.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.zentsova.conveyor.model.LoanApplicationRequestDto;
import ru.zentsova.conveyor.util.exceptions.ApplicationException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Class LoanApplicationRequestDtoValidatorTest
 */
class LoanApplicationRequestDtoValidatorTest {

    LoanApplicationRequestDtoValidator validator;

    @BeforeEach
    public void init() throws IllegalAccessException, NoSuchFieldException {
        validator = new LoanApplicationRequestDtoValidator();
        setField(validator, "amountRequestedMin", new BigDecimal("10000.00"));
        setField(validator, "termMin", 6);
        setField(validator, "ageMin", 21);
        setField(validator, "regexPassportNumber", "\\d{6}");
        setField(validator, "regexPassportSeries", "\\d{4}");
        setField(validator, "regexName", "([a-zA-Z]{2,30})");
        setField(validator, "regexEmail", "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
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
                "birthdate - Should be more or equal than 21; email - Should be like test@test.com; passportSeries - Length must be 4 digits; " +
                "passportNumber - Length must be 6 digits; ";
        String actualMessage = ex.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testValidate_shouldReturnTrue_whenAllDtoParamsIsValid() {
        LoanApplicationRequestDto dto = createLoanApplicationRequestDto(new BigDecimal("300000.00"), 60, "Anastasiia", "Zentsova", "Olegovna",
                "username@domain.co.in", LocalDate.of(1992, 2, 4), "4444", "666666");

        assertEquals(true, validator.validate(dto, true));
    }

    private LoanApplicationRequestDto createLoanApplicationRequestDto(BigDecimal requestedAmount, int term, String firstName, String lastName, String middleName,
        String email, LocalDate birthdate, String passportSeries, String passportNumber)
    {
        return new LoanApplicationRequestDto().amount(requestedAmount).term(term).firstName(firstName).lastName(lastName).middleName(middleName).email(email)
                .birthdate(birthdate).passportSeries(passportSeries).passportNumber(passportNumber);
    }

    private void setField(LoanApplicationRequestDtoValidator validator, String filedName, Object fieldValue) throws IllegalAccessException, NoSuchFieldException {
        Field field = validator.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        field.set(validator, fieldValue);
    }
}