package ru.zentsova.conveyor.util.validator;

import org.springframework.stereotype.Service;
import ru.zentsova.conveyor.model.LoanApplicationRequestDto;
import ru.zentsova.conveyor.util.exceptions.ApplicationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Class LoanApplicationRequestDTOValidator
 * Validation input data (pre-scoring)
 */
@Service
public class LoanApplicationRequestDtoValidator {

    /** Field names */
    private static final String S_AMOUNT = "amount";
    private static final String S_BIRTH_DATE = "birthdate";
    private static final String S_EMAIL = "email";
    private static final String S_FIRST_NAME = "firstName";
    private static final String S_LAST_NAME = "lastName";
    private static final String S_MIDDLE_NAME = "middleName";
    private static final String S_PASSPORT_NUMBER = "passportNumber";
    private static final String S_PASSPORT_SERIES = "passportSeries";
    private static final String S_TERM = "term";

    private static final String S_EMAIL_PATTERN = "Should be like test@test.com";
    private static final String S_SHOULD_BE_EQUAL_OR_MORE_THAN = "Should be more or equal than %s";
    private static final String S_SHOULD_NOT_BE_EMPTY = "Should not be empty";
    private static final String S_PART_OF_NAME_MUST_BE_RIGHT_LENGTH = "Must be more than 2 and less than 30 characters and looks like %s";
    private static final String S_PART_OF_PASSPORT_LENGTH_DIGITS = "Length must be %s digits";
    private static final String S_FIRST_NAME_PATTERN = "Ivan";
    private static final String S_LAST_NAME_PATTERN = "Ivanov";
    private static final String S_MIDDLE_NAME_PATTERN = "Ivanovich";

    /** RegEx */
    private static final String REGEX_EMAIL = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String REGEX_PART_OF_NAME = "([a-zA-Z]{2,30})";
    private static final String REGEX_PASSPORT_NUMBER = "\\d{6}";
    private static final String REGEX_PASSPORT_SERIES = "\\d{4}";

    private static final String S_TEN_THOUSAND = "10000.00";
    private static final BigDecimal AMOUNT = new BigDecimal(S_TEN_THOUSAND);
    private static final int PASSPORT_SERIES_LENGTH = 4;
    private static final int PASSPORT_NUMBER_LENGTH = 6;
    private static final int MIN_TERM = 6;
    private static final int MIN_AGE = 21;

    private static final String S_SEMICOLON = "; ";
    private static final String S_SEPARATOR = " - ";

    /**
     * Validate (pre-scoring) input loan application request DTO
     * @param dto                      input loan application request dto
     * @param isAmountRequired         is field amount required
     * @param isFirstNameRequired      is field first name required
     * @param isLastNameRequired       is field last name required
     * @param isMiddleNameRequired     is field middle name required
     * @param isTermRequired           is field term required
     * @param isBirthdateRequired      is field birthdate required
     * @param isEmailRequired          is field email required
     * @param isPassportSeriesRequired is field passport series required
     * @param isPassportNumberRequired is field passport number required
     * @return true, if data id valid
     * @throws ApplicationException, if data is not valid
     */
    public boolean validate(LoanApplicationRequestDto dto, boolean isAmountRequired, boolean isFirstNameRequired, boolean isLastNameRequired, boolean isMiddleNameRequired,
        boolean isTermRequired, boolean isBirthdateRequired, boolean isEmailRequired, boolean isPassportSeriesRequired, boolean isPassportNumberRequired)
        throws ApplicationException
    {
        StringBuilder errorMsg = new StringBuilder();

        checkAmount(dto.getAmount(), errorMsg, isAmountRequired);
        checkPartOfName(dto.getFirstName(), S_FIRST_NAME, S_FIRST_NAME_PATTERN, errorMsg, isFirstNameRequired);
        checkPartOfName(dto.getLastName(), S_LAST_NAME, S_LAST_NAME_PATTERN, errorMsg, isLastNameRequired);
        checkPartOfName(dto.getMiddleName(), S_MIDDLE_NAME, S_MIDDLE_NAME_PATTERN, errorMsg, isMiddleNameRequired);
        checkTerm(dto.getTerm(), errorMsg, isTermRequired);
        checkBirthdate(dto.getBirthdate(), errorMsg, isBirthdateRequired);
        checkEmail(dto.getEmail(), errorMsg, isEmailRequired);
        checkPassportSeries(dto.getPassportSeries(), errorMsg, isPassportSeriesRequired);
        checkPassportNumber(dto.getPassportNumber(), errorMsg, isPassportNumberRequired);

        if (!errorMsg.toString().isBlank())
            throw new ApplicationException(errorMsg.toString());

        return true;
    }

    private void checkAmount(BigDecimal amount, StringBuilder errorMsg, Boolean required) {
        if (required)
            checkNotNull(amount, S_AMOUNT, errorMsg);
        if (Objects.nonNull(amount) && amount.compareTo(AMOUNT) < 0)
            errorMsg.append(S_AMOUNT).append(S_SEPARATOR).append(String.format(S_SHOULD_BE_EQUAL_OR_MORE_THAN, S_TEN_THOUSAND)).append(S_SEMICOLON);
    }

    private void checkPartOfName(String partOfName, String fieldName, String pattern, StringBuilder errorMsg, Boolean required) {
        if (required)
            checkNotNull(partOfName, fieldName, errorMsg);
        if (Objects.nonNull(partOfName) && !partOfName.matches(REGEX_PART_OF_NAME))
            errorMsg.append(fieldName).append(S_SEPARATOR).append(String.format(S_PART_OF_NAME_MUST_BE_RIGHT_LENGTH, pattern)).append(S_SEMICOLON);
    }

    private void checkTerm(Integer term, StringBuilder errorMsg, Boolean required) {
        if (required)
            checkNotNull(term, S_TERM, errorMsg);
        if (Objects.nonNull(term) && term < MIN_TERM)
            errorMsg.append(S_TERM).append(S_SEPARATOR).append(String.format(S_SHOULD_BE_EQUAL_OR_MORE_THAN, MIN_TERM)).append(S_SEMICOLON);
    }

    private void checkBirthdate(LocalDate birthdate,StringBuilder errorMsg, Boolean required) {
        if (required)
            checkNotNull(birthdate, S_BIRTH_DATE,  errorMsg);
        if (Objects.nonNull(birthdate) && (Period.between(birthdate, LocalDate.now()).getYears() < MIN_AGE))
            errorMsg.append(S_BIRTH_DATE).append(S_SEPARATOR).append(String.format(S_SHOULD_BE_EQUAL_OR_MORE_THAN, MIN_AGE)).append(S_SEMICOLON);
    }

    private void checkEmail(String email, StringBuilder errorMsg, Boolean required) {
        if (required)
            checkNotNull(email, S_EMAIL, errorMsg);
        if (Objects.nonNull(email) && !email.matches(REGEX_EMAIL))
            errorMsg.append(S_EMAIL).append(S_SEPARATOR).append(S_EMAIL_PATTERN).append(S_SEMICOLON);
    }

    private void checkPassportSeries(String passportSeries, StringBuilder errorMsg, Boolean required) {
        if (required)
            checkNotNull(passportSeries, S_PASSPORT_SERIES, errorMsg);
        if (Objects.nonNull(passportSeries) && !passportSeries.matches(REGEX_PASSPORT_SERIES))
            errorMsg.append(S_PASSPORT_SERIES).append(S_SEPARATOR).append(String.format(S_PART_OF_PASSPORT_LENGTH_DIGITS, PASSPORT_SERIES_LENGTH)).append(S_SEMICOLON);
    }

    private void checkPassportNumber(String passportNumber, StringBuilder errorMsg, Boolean required) {
        if (required)
            checkNotNull(passportNumber, S_PASSPORT_NUMBER, errorMsg);
        if (Objects.nonNull(passportNumber) && !passportNumber.matches(REGEX_PASSPORT_NUMBER))
            errorMsg.append(S_PASSPORT_NUMBER).append(S_SEPARATOR).append(String.format(S_PART_OF_PASSPORT_LENGTH_DIGITS, PASSPORT_NUMBER_LENGTH)).append(S_SEMICOLON);
    }

    private void checkNotNull(Object field, String fieldName, StringBuilder errorMsg) {
        if (Objects.isNull(field))
            errorMsg.append(fieldName).append(S_SEPARATOR).append(S_SHOULD_NOT_BE_EMPTY).append(S_SEMICOLON);
    }
}