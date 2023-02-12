package ru.zentsova.application.util.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.zentsova.application.config.properties.PrescoringProperties;
import ru.zentsova.application.model.LoanApplicationRequestDto;
import ru.zentsova.application.exceptions.ApplicationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Validating input data (pre-scoring)
 */
@Service
@Slf4j
public class LoanApplicationRequestDtoValidator {

    private final PrescoringProperties prescoreProps;

    @Autowired
    public LoanApplicationRequestDtoValidator(PrescoringProperties prescoreProps) {
        this.prescoreProps = prescoreProps;
    }

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

    private static final String S_AGE_SHOULD_BE_EQUAL_OR_MORE_THAN = "Age should be more or equal than %s";
    private static final String S_EMAIL_PATTERN = "Should be like test@test.com";
    private static final String S_SHOULD_BE_EQUAL_OR_MORE_THAN = "Should be more or equal than %s";
    private static final String S_SHOULD_NOT_BE_EMPTY = "Should not be empty";
    private static final String S_PART_OF_NAME_MUST_BE_RIGHT_LENGTH = "Must be more than 2 and less than 30 characters and looks like %s";
    private static final String S_PART_OF_PASSPORT_LENGTH_DIGITS = "Length must be %s digits";
    private static final String S_FIRST_NAME_PATTERN = "Ivan";
    private static final String S_LAST_NAME_PATTERN = "Ivanov";
    private static final String S_MIDDLE_NAME_PATTERN = "Ivanovich";

    private static final int PASSPORT_SERIES_LENGTH = 4;
    private static final int PASSPORT_NUMBER_LENGTH = 6;

    private static final String S_SEMICOLON = "; ";
    private static final String S_SEPARATOR = " - ";

    /**
     * Validate (pre-scoring) input loan application request DTO
     * @param dto                      input loan application request dto
     * @param isMiddleNameRequired     is field middle name required
     * @return true, if data id valid
     * @throws ApplicationException, if data is not valid
     */
    public boolean validate(LoanApplicationRequestDto dto, boolean isMiddleNameRequired) throws ApplicationException {
        StringBuilder errorMsg = new StringBuilder();

        checkAmount(dto.getAmount(), errorMsg);
        checkFirstName(dto.getFirstName(), errorMsg);
        checkLastName(dto.getLastName(), errorMsg);
        checkMiddleName(dto.getMiddleName(), errorMsg, isMiddleNameRequired);
        checkTerm(dto.getTerm(), errorMsg);
        checkBirthdate(dto.getBirthdate(), errorMsg);
        checkEmail(dto.getEmail(), errorMsg);
        checkPassportSeries(dto.getPassportSeries(), errorMsg);
        checkPassportNumber(dto.getPassportNumber(), errorMsg);

        if (!errorMsg.toString().isBlank()) {
            log.warn("Prescoring failed");
            throw new ApplicationException(errorMsg.toString());
        }

        return true;
    }

    private void checkAmount(BigDecimal amount, StringBuilder errorMsg) {
        if (checkNotNull(amount, S_AMOUNT, errorMsg) && amount.compareTo(prescoreProps.getAmount().getRequestedMin()) < 0)
            errorMsg.append(S_AMOUNT).append(S_SEPARATOR).append(String.format(S_SHOULD_BE_EQUAL_OR_MORE_THAN, prescoreProps.getAmount().getRequestedMin().toString())).append(S_SEMICOLON);
    }

    private void checkFirstName(String firstName, StringBuilder errorMsg) {
        if (checkNotNull(firstName, S_FIRST_NAME, errorMsg) && !firstName.matches(prescoreProps.getRegex().getName()))
            errorMsg.append(S_FIRST_NAME).append(S_SEPARATOR).append(String.format(S_PART_OF_NAME_MUST_BE_RIGHT_LENGTH, S_FIRST_NAME_PATTERN)).append(S_SEMICOLON);
    }

    private void checkLastName(String lastName, StringBuilder errorMsg) {
        if (checkNotNull(lastName, S_LAST_NAME, errorMsg) && !lastName.matches(prescoreProps.getRegex().getName()))
            errorMsg.append(S_LAST_NAME).append(S_SEPARATOR).append(String.format(S_PART_OF_NAME_MUST_BE_RIGHT_LENGTH, S_LAST_NAME_PATTERN)).append(S_SEMICOLON);
    }

    private void checkMiddleName(String middleName, StringBuilder errorMsg, Boolean required) {
        if (required)
            checkNotNull(middleName, S_MIDDLE_NAME, errorMsg);
        if ((middleName != null && !middleName.isBlank()) && !middleName.matches(prescoreProps.getRegex().getName()))
            errorMsg.append(S_MIDDLE_NAME).append(S_SEPARATOR).append(String.format(S_PART_OF_NAME_MUST_BE_RIGHT_LENGTH, S_MIDDLE_NAME_PATTERN)).append(S_SEMICOLON);
    }

    private void checkTerm(Integer term, StringBuilder errorMsg) {
        if (checkNotNull(term, S_TERM, errorMsg) && term < prescoreProps.getTerm().getMin())
            errorMsg.append(S_TERM).append(S_SEPARATOR).append(String.format(S_SHOULD_BE_EQUAL_OR_MORE_THAN, prescoreProps.getTerm().getMin())).append(S_SEMICOLON);
    }

    private void checkBirthdate(LocalDate birthdate,StringBuilder errorMsg) {
        if (checkNotNull(birthdate, S_BIRTH_DATE,  errorMsg) && (Period.between(birthdate, LocalDate.now()).getYears() < prescoreProps.getAge().getMin()))
            errorMsg.append(S_BIRTH_DATE).append(S_SEPARATOR).append(String.format(S_AGE_SHOULD_BE_EQUAL_OR_MORE_THAN, prescoreProps.getAge().getMin())).append(S_SEMICOLON);
    }

    private void checkEmail(String email, StringBuilder errorMsg) {
        if (checkNotNull(email, S_EMAIL, errorMsg) && !email.matches(prescoreProps.getRegex().getEmail()))
            errorMsg.append(S_EMAIL).append(S_SEPARATOR).append(S_EMAIL_PATTERN).append(S_SEMICOLON);
    }

    private void checkPassportSeries(String passportSeries, StringBuilder errorMsg) {
        if (checkNotNull(passportSeries, S_PASSPORT_SERIES, errorMsg) && !passportSeries.matches(prescoreProps.getRegex().getPassport().getSeries()))
            errorMsg.append(S_PASSPORT_SERIES).append(S_SEPARATOR).append(String.format(S_PART_OF_PASSPORT_LENGTH_DIGITS, PASSPORT_SERIES_LENGTH)).append(S_SEMICOLON);
    }

    private void checkPassportNumber(String passportNumber, StringBuilder errorMsg) {
        if (checkNotNull(passportNumber, S_PASSPORT_NUMBER, errorMsg) && !passportNumber.matches(prescoreProps.getRegex().getPassport().getNumber()))
            errorMsg.append(S_PASSPORT_NUMBER).append(S_SEPARATOR).append(String.format(S_PART_OF_PASSPORT_LENGTH_DIGITS, PASSPORT_NUMBER_LENGTH)).append(S_SEMICOLON);
    }

    private boolean checkNotNull(Object field, String fieldName, StringBuilder errorMsg) {
        if (Objects.isNull(field) || (field instanceof String && ((String) field).isBlank())) {
            errorMsg.append(fieldName).append(S_SEPARATOR).append(S_SHOULD_NOT_BE_EMPTY).append(S_SEMICOLON);
            return false;
        }
        return true;
    }
}