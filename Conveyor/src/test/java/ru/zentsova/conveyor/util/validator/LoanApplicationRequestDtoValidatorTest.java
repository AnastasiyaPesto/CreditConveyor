package ru.zentsova.conveyor.util.validator;

import org.junit.jupiter.api.Test;
import ru.zentsova.conveyor.model.LoanApplicationRequestDto;
import ru.zentsova.conveyor.util.exceptions.ApplicationException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class LoanApplicationRequestDtoValidatorTest
 */
class LoanApplicationRequestDtoValidatorTest {

    @Test
    public void testValidate_shouldReturnTrue_whenAllDtoParamsIsEmpty() {
        LoanApplicationRequestDtoValidator validator = new LoanApplicationRequestDtoValidator();
        LoanApplicationRequestDto dto = new LoanApplicationRequestDto();

        boolean isValid = validator.validate(dto, false, false, false, false, false, false, false, false, false);

        assertTrue(isValid);
    }

    @Test
    public void testValidate_shouldExceptionThrown_whenAllDtoParamsIsEmptyButShouldBeFilled() {
        LoanApplicationRequestDtoValidator validator = new LoanApplicationRequestDtoValidator();
        LoanApplicationRequestDto dto = new LoanApplicationRequestDto();

        Exception ex = assertThrows(ApplicationException.class, () -> {
            validator.validate(dto, true, true, true, true, true, true, true, true, true);
        });

        String expectedMessage = "amount - Should not be empty; firstName - Should not be empty; lastName - Should not be empty; middleName - Should not be empty; " +
                "term - Should not be empty; birthdate - Should not be empty; email - Should not be empty; passportSeries - Should not be empty; " +
                "passportNumber - Should not be empty; ";
        String actualMessage = ex.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testValidate_shouldExceptionThrown_whenAllDtoParamsIsNotValid() {
        LoanApplicationRequestDtoValidator validator = new LoanApplicationRequestDtoValidator();
        LoanApplicationRequestDto dto = createLoanApplicationRequestDto(new BigDecimal("9000.00"), 3, "a", "a", "a", "test@", LocalDate.of(2020, 1, 1), "1", "1");

        Exception ex = assertThrows(ApplicationException.class, () -> {
            validator.validate(dto, true, true, true, true, true, true, true, true, true);
        });

        String expectedMessage = "amount - Should be more or equal than 10000.00; firstName - Must be more than 2 and less than 30 characters and looks like Ivan; " +
                "lastName - Must be more than 2 and less than 30 characters and looks like Ivanov; " +
                "middleName - Must be more than 2 and less than 30 characters and looks like Ivanovich; term - Should be more or equal than 6; " +
                "birthdate - Should be more or equal than 21; email - Should be like test@test.com; passportSeries - Length must be 4 digits; " +
                "passportNumber - Length must be 6 digits; ";
        String actualMessage = ex.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    private LoanApplicationRequestDto createLoanApplicationRequestDto(BigDecimal requestedAmount, int term, String firstName, String lastName, String middleName,
        String email, LocalDate birthdate, String passportSeries, String passportNumber)
    {
        return new LoanApplicationRequestDto().amount(requestedAmount).term(term).firstName(firstName).lastName(lastName).middleName(middleName).email(email)
                .birthdate(birthdate).passportSeries(passportSeries).passportNumber(passportNumber);
    }
}