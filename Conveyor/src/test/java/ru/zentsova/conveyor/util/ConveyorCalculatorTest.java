package ru.zentsova.conveyor.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.zentsova.conveyor.model.EmploymentDto;
import ru.zentsova.conveyor.model.PaymentScheduleElement;
import ru.zentsova.conveyor.model.ScoringDataDto;
import ru.zentsova.conveyor.util.exceptions.LoanDecisionException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class ConveyorCalculatorTest
 */
class ConveyorCalculatorTest {

    ConveyorCalculator calculator;

    @BeforeEach
    public void init() throws NoSuchFieldException, IllegalAccessException {
        calculator = new ConveyorCalculator();
        setField(calculator, "rateBase", new BigDecimal("12.00"));
        setField(calculator, "rateMin", new BigDecimal("5.80"));
        setField(calculator, "rateMax", new BigDecimal("18.50"));
        setField(calculator, "rateInsurance", new BigDecimal("0.02"));
        setField(calculator, "rateLoweringInsurance", new BigDecimal("1.00"));
        setField(calculator, "rateLoweringSalaryClient", new BigDecimal("0.60"));
        setField(calculator, "ageMin", 21);
        setField(calculator, "ageMax", 60);
        setField(calculator, "workExperienceTotalMin", 6);
        setField(calculator, "workExperienceCurrentMin", 12);
    }

    @Test
    public void testGetApplicationId_shouldIncrease_whenMethodCalledSeveralTimes() {
        assertEquals(1, calculator.getApplicationId());
        assertEquals(2, calculator.getApplicationId());
        assertEquals(3, calculator.getApplicationId());
    }

    @Test
    public void testCalcRate() {
        assertEquals(new BigDecimal("10.40"), calculator.calcRate(true, true));
        assertEquals(new BigDecimal("11.00"), calculator.calcRate(false, true));
        assertEquals(new BigDecimal("11.40"), calculator.calcRate(true, false));
        assertEquals(new BigDecimal("12.00"), calculator.calcRate(false, false));
    }

    @Test
    public void testCalcTotalAmount() {
        assertEquals(new BigDecimal("1000.00"), calculator.calcTotalAmount(new BigDecimal("1000.00"), 12, false));
        assertEquals(new BigDecimal("306000.00"), calculator.calcTotalAmount(new BigDecimal("300000.00"), 12, true));
    }

    @Test
    public void testCalcMonthlyPayment() {
        assertEquals(new BigDecimal("6522.73"), calculator.calcMonthlyPayment(new BigDecimal("11.0"), 60, new BigDecimal("300000.00")));
    }

    @Test
    public void testCalcPskInPercent() {
        assertEquals(new BigDecimal("6.00"), calculator.calcPskInPercent(new BigDecimal("300000.00"), new BigDecimal("390000.00"), 60));
    }

    @Test
    public void testGetPaymentSchedule_shouldBeEmptyAndNotNull_whenTermIsZero() {
        List<PaymentScheduleElement> paymentSchedule = calculator.getPaymentSchedule(new BigDecimal("300000.00"), new BigDecimal("6500"), new BigDecimal("11.00"), 0);

        assertNotNull(paymentSchedule);
        assertEquals(0, paymentSchedule.size());
    }

    @Test
    public void testGetPaymentSchedule_shouldNotNullAndBeNotEmpty_whenTermIsNotZero() {
        List<PaymentScheduleElement> paymentSchedule = calculator.getPaymentSchedule(new BigDecimal("300000.00"), new BigDecimal("6500"), new BigDecimal("11.00"), 12);

        assertNotNull(paymentSchedule);
        assertTrue(paymentSchedule.size() != 0);
    }

    @Test
    public void testCalcScoreRate_shouldExceptionThrown_whenAgeIsNotValid() {
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(2020, 1, 1));

        Exception ex = assertThrows(LoanDecisionException.class, () -> calculator.calcScoreRate(dto));

        String expectedMessage = "Unfortunately, you were denied a loan";
        String actualMessage = ex.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCalcScoreRate_shouldExceptionThrown_whenUnemployed() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.UNEMPLOYED);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).employment(emp);

        Exception ex = assertThrows(LoanDecisionException.class, () -> calculator.calcScoreRate(dto));

        String expectedMessage = "Unfortunately, you were denied a loan";
        String actualMessage = ex.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCalcScoreRate_shouldExceptionThrown_whenSalaryIsNotEnough() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.EMPLOYED).salary(new BigDecimal("5000.00"));
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp);

        Exception ex = assertThrows(LoanDecisionException.class, () -> calculator.calcScoreRate(dto));

        String expectedMessage = "Unfortunately, you were denied a loan";
        String actualMessage = ex.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCalcScoreRate_shouldExceptionThrown_whenWorkExperienceCurrentIsNotEnough() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.EMPLOYED).salary(new BigDecimal("50000.00")).workExperienceCurrent(3);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp);

        Exception ex = assertThrows(LoanDecisionException.class, () -> calculator.calcScoreRate(dto));

        String expectedMessage = "Unfortunately, you were denied a loan";
        String actualMessage = ex.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCalcScoreRate_shouldExceptionThrown_whenWorkExperienceTotalIsNotEnough() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.EMPLOYED).salary(new BigDecimal("50000.00"))
                .workExperienceCurrent(3).workExperienceTotal(10);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp);

        Exception ex = assertThrows(LoanDecisionException.class, () -> calculator.calcScoreRate(dto));

        String expectedMessage = "Unfortunately, you were denied a loan";
        String actualMessage = ex.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCalcScoreRate_shouldReturnValidRate_whenEmployedTraineeMarriedNotDependentAmount() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.EMPLOYED).salary(new BigDecimal("50000.00"))
                .workExperienceCurrent(7).workExperienceTotal(24).position(EmploymentDto.PositionEnum.TRAINEE);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp)
                .isInsuranceEnabled(false).isSalaryClient(false).maritalStatus(ScoringDataDto.MaritalStatusEnum.MARRIED).dependentAmount(0);

        BigDecimal rate = calculator.calcScoreRate(dto);

        assertEquals(new BigDecimal("11.00"), rate);
    }

    @Test
    public void testCalcScoreRate_shouldReturnValidRate_whenEmployedDeveloperMarriedNotDependentAmount() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.EMPLOYED).salary(new BigDecimal("50000.00"))
                .workExperienceCurrent(7).workExperienceTotal(24).position(EmploymentDto.PositionEnum.DEVELOPER);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp)
                .isInsuranceEnabled(false).isSalaryClient(false).maritalStatus(ScoringDataDto.MaritalStatusEnum.MARRIED).dependentAmount(0);

        BigDecimal rate = calculator.calcScoreRate(dto);

        assertEquals(new BigDecimal("7.00"), rate);
    }

    @Test
    public void testCalcScoreRate_shouldReturnRateMin_whenEmployedTopLevelManagerMarriedNotDependentAmount() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.EMPLOYED).salary(new BigDecimal("50000.00"))
                .workExperienceCurrent(7).workExperienceTotal(24).position(EmploymentDto.PositionEnum.TOP_LEVEL_MANAGER);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp)
                .isInsuranceEnabled(false).isSalaryClient(false).maritalStatus(ScoringDataDto.MaritalStatusEnum.MARRIED).dependentAmount(0);

        BigDecimal rate = calculator.calcScoreRate(dto);

        assertEquals(new BigDecimal("5.80"), rate);
    }

    @Test
    public void testCalcScoreRate_shouldReturnValidRate_whenEmployedDeveloperSingleNotDependentAmount() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.EMPLOYED).salary(new BigDecimal("50000.00"))
                .workExperienceCurrent(7).workExperienceTotal(24).position(EmploymentDto.PositionEnum.DEVELOPER);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp)
                .isInsuranceEnabled(false).isSalaryClient(false).maritalStatus(ScoringDataDto.MaritalStatusEnum.SINGLE).dependentAmount(0);

        BigDecimal rate = calculator.calcScoreRate(dto);

        assertEquals(new BigDecimal("11.00"), rate);
    }

    @Test
    public void testCalcScoreRate_shouldReturnValidRate_whenEmployedDeveloperMarriedMoreThanOneDependentAmount() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.EMPLOYED).salary(new BigDecimal("50000.00"))
                .workExperienceCurrent(7).workExperienceTotal(24).position(EmploymentDto.PositionEnum.DEVELOPER);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp)
                .isInsuranceEnabled(false).isSalaryClient(false).maritalStatus(ScoringDataDto.MaritalStatusEnum.MARRIED).dependentAmount(2);

        BigDecimal rate = calculator.calcScoreRate(dto);

        assertEquals(new BigDecimal("8.00"), rate);
    }

    @Test
    public void testCalcScoreRate_shouldReturnValidRate_whenSelfEmployedWidowerMoreThanOneDependentAmount() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.SELF_EMPLOYED).salary(new BigDecimal("50000.00"))
                .workExperienceCurrent(7).workExperienceTotal(24);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp)
                .isInsuranceEnabled(false).isSalaryClient(false).maritalStatus(ScoringDataDto.MaritalStatusEnum.WIDOWER).dependentAmount(2);

        BigDecimal rate = calculator.calcScoreRate(dto);

        assertEquals(new BigDecimal("13.00"), rate);
    }

    @Test
    public void testCalcScoreRate_shouldReturnRateMin_whenEmployedTopLevelManagerMarriedOneDependentAmount() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.EMPLOYED).salary(new BigDecimal("50000.00"))
                .workExperienceCurrent(7).workExperienceTotal(24).position(EmploymentDto.PositionEnum.TOP_LEVEL_MANAGER);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp)
                .isInsuranceEnabled(true).isSalaryClient(true).maritalStatus(ScoringDataDto.MaritalStatusEnum.MARRIED).dependentAmount(1);

        BigDecimal rate = calculator.calcScoreRate(dto);

        assertEquals(new BigDecimal("5.80"), rate);
    }

    @Test
    public void testCalcScoreRate_shouldReturnValidRate_whenBusinessOwnerMarriedMarriedOneDependentAmount() {
        EmploymentDto emp = new EmploymentDto().employmentStatus(EmploymentDto.EmploymentStatusEnum.BUSINESS_OWNER).salary(new BigDecimal("50000.00"))
                .workExperienceCurrent(7).workExperienceTotal(24);
        ScoringDataDto dto = new ScoringDataDto().birthdate(LocalDate.of(1991, 1, 1)).amount(new BigDecimal("300000.00")).employment(emp)
                .isInsuranceEnabled(true).isSalaryClient(true).maritalStatus(ScoringDataDto.MaritalStatusEnum.MARRIED).dependentAmount(1);

        BigDecimal rate = calculator.calcScoreRate(dto);

        assertEquals(new BigDecimal("10.40"), rate);
    }

    private void setField(ConveyorCalculator conveyorCalculator, String filedName, Object fieldValue) throws IllegalAccessException, NoSuchFieldException {
        Field field = conveyorCalculator.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        field.set(conveyorCalculator, fieldValue);
    }
}