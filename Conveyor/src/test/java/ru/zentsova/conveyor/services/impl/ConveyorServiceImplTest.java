package ru.zentsova.conveyor.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.zentsova.conveyor.model.*;
import ru.zentsova.conveyor.util.ConveyorCalculator;
import ru.zentsova.conveyor.util.validator.LoanApplicationRequestDtoValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Class ConveyorServiceImplTest
 */
@ExtendWith(MockitoExtension.class)
class ConveyorServiceImplTest {

    private BigDecimal monthlyPayment;
    private BigDecimal totalAmount;
    private BigDecimal requestedAmount;
    private BigDecimal salary;
    private LocalDate birthdate;
    private LocalDate passportIssueDate;
    private int term;
    private int dependentAmount;
    private int workExperienceCurrent;
    private int workExperienceTotal;
    private String firstName;
    private String secondName;
    private String middleName;
    private String email;
    private String passportSeries;
    private String passportNumber;
    private String employerINN;
    private String account;
    private String passportIssueBranch;

    @Mock
    ConveyorCalculator calculator;
    @Mock
    LoanApplicationRequestDtoValidator validator;

    @InjectMocks
    ConveyorServiceImpl conveyorService;

    @BeforeEach
    public void init() {
        monthlyPayment = new BigDecimal("8000.00");
        requestedAmount = new BigDecimal("50000.00");
        totalAmount = requestedAmount.add(new BigDecimal("15000.00"));
        salary = new BigDecimal("35000.00");
        firstName = "Ivan";
        secondName = "Ivanov";
        middleName = "Ivanovich";
        email = "ivan@test.ru";
        birthdate = LocalDate.of(1992, 2, 4);
        passportIssueDate = LocalDate.of(2019, 5, 31);
        passportSeries = "1234";
        passportNumber = "556677";
        passportIssueBranch = "100-001";
        term = 60;
        dependentAmount = 1;
        employerINN = "98765432109";
        account = "40819810570000123456";
        workExperienceCurrent = 7;
        workExperienceTotal = 39;
    }

    @Test
    public void testGetAllPossibleOffers_shouldBeNotEmpty_whenRequestVerified() {
        LoanApplicationRequestDto requestDto = createLoanApplicationRequestDto(requestedAmount, term, firstName, secondName, middleName,
                email, birthdate, passportSeries, passportNumber);

        when(validator.validate(any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(true);

        when(calculator.getApplicationId()).thenReturn(1L);
        when(calculator.calcRate(anyBoolean(), anyBoolean())).thenReturn(new BigDecimal("15"));
        when(calculator.calcTotalAmount(any(BigDecimal.class), anyInt(), anyBoolean())).thenReturn(totalAmount);

        List<LoanOfferDto> offers = conveyorService.getAllPossibleOffers(requestDto);

        assertNotNull(offers);
        verify(calculator, times(4)).getApplicationId();
        verify(calculator, times(4)).calcRate(anyBoolean(), anyBoolean());
        verify(calculator, times(4)).calcTotalAmount(any(BigDecimal.class), anyInt(), anyBoolean());
        verify(calculator, times(4)).calcMonthlyPayment(any(BigDecimal.class), anyInt(), any(BigDecimal.class));
    }

    @Test
    public void testGetAllPossibleOffers_shouldBeEmpty_whenRequestNotVerified() {
        LoanApplicationRequestDto request = new LoanApplicationRequestDto();

        when(validator.validate(any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(false);

        List<LoanOfferDto> offers = conveyorService.getAllPossibleOffers(request);
        assertEquals(0, offers.size());
        verify(calculator, times(0)).getApplicationId();
        verify(calculator, times(0)).calcRate(anyBoolean(), anyBoolean());
        verify(calculator, times(0)).calcTotalAmount(any(BigDecimal.class), anyInt(), anyBoolean());
        verify(calculator, times(0)).calcMonthlyPayment(any(BigDecimal.class), anyInt(), any(BigDecimal.class));
    }

    @Test
    public void testGetLoanConditions_shouldBeNotNullWithPaymentSchedule() {
        EmploymentDto employment = createEmployment(employerINN, EmploymentDto.EmploymentStatusEnum.EMPLOYED, EmploymentDto.PositionEnum.DEVELOPER, salary,
                workExperienceCurrent, workExperienceTotal);
        ScoringDataDto scoringData = createScoringDataDto(firstName, secondName, middleName, account, passportSeries, passportNumber, passportIssueBranch,
                passportIssueDate, birthdate, requestedAmount, term, dependentAmount, true, true, ScoringDataDto.GenderEnum.FEMALE, ScoringDataDto.MaritalStatusEnum.MARRIED,
                employment);


        when(calculator.calcScoreRate(scoringData)).thenReturn(new BigDecimal("10.0"));
        when(calculator.calcTotalAmount(any(BigDecimal.class), anyInt(), anyBoolean())).thenReturn(totalAmount);
        when(calculator.calcMonthlyPayment(any(BigDecimal.class), anyInt(), any(BigDecimal.class))).thenReturn(monthlyPayment);
        when(calculator.getPaymentSchedule(any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), anyInt())).thenReturn(createPaymentSchedule());
        when(calculator.calcPskInPercent(any(BigDecimal.class), any(BigDecimal.class), anyInt())).thenReturn(new BigDecimal("11.5"));

        CreditDto loanConditions = conveyorService.getLoanConditions(scoringData);

        verify(calculator, times(1)).calcScoreRate(scoringData);
        verify(calculator, times(1)).calcTotalAmount(any(BigDecimal.class), anyInt(), anyBoolean());
        verify(calculator, times(1)).calcMonthlyPayment(any(BigDecimal.class), anyInt(), any(BigDecimal.class));
        verify(calculator, times(1)).getPaymentSchedule(any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), anyInt());
        verify(calculator, times(1)).calcPskInPercent(any(BigDecimal.class), any(BigDecimal.class), anyInt());

        assertNotNull(loanConditions);
        assertNotNull(loanConditions.getPaymentSchedule());
        assertEquals(7, loanConditions.getPaymentSchedule().size());
        List<PaymentScheduleElement> paymentSchedule = createPaymentSchedule();
        assertEquals(loanConditions.getPaymentSchedule(), paymentSchedule);
        assertEquals(loanConditions.getPsk(), new BigDecimal("11.5"));
        assertEquals(loanConditions.getTerm(), term);
        assertEquals(loanConditions.getMonthlyPayment(), monthlyPayment);
        assertEquals(loanConditions.getRate(), new BigDecimal("10.0"));
        BigDecimal allInterestPayment = paymentSchedule.stream().map(PaymentScheduleElement::getInterestPayment).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(loanConditions.getAmount(), totalAmount.add(allInterestPayment));
        assertEquals(loanConditions.getIsInsuranceEnabled(), true);
        assertEquals(loanConditions.getIsSalaryClient(), true);
    }

    private LoanApplicationRequestDto createLoanApplicationRequestDto(BigDecimal requestedAmount, int term, String firstName, String lastName, String middleName,
        String email, LocalDate birthdate, String passportSeries, String passportNumber)
    {
        return new LoanApplicationRequestDto().amount(requestedAmount).term(term).firstName(firstName).lastName(lastName).middleName(middleName).email(email)
                .birthdate(birthdate).passportSeries(passportSeries).passportNumber(passportNumber);
    }

    private ScoringDataDto createScoringDataDto(String firstName, String lastName, String middleName, String account, String passportNumber, String passportSeries,
        String passportIssueBranch, LocalDate passportIssueDate, LocalDate birthdate, BigDecimal amount, int term, int dependentAmount, boolean isInsuranceEnabled,
        boolean isSalaryClient, ScoringDataDto.GenderEnum gender, ScoringDataDto.MaritalStatusEnum maritalStatus, EmploymentDto employment)
    {
        return new ScoringDataDto().amount(amount).term(term).firstName(firstName).lastName(lastName).middleName(middleName).gender(gender).birthdate(birthdate)
                .passportSeries(passportSeries).passportNumber(passportNumber).passportIssueDate(passportIssueDate).passportIssueBranch(passportIssueBranch)
                .maritalStatus(maritalStatus).dependentAmount(dependentAmount).employment(employment).account(account).isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient);
    }

    private EmploymentDto createEmployment(String employerINN, EmploymentDto.EmploymentStatusEnum employmentStatus, EmploymentDto.PositionEnum position,
        BigDecimal salary, int workExperienceCurrent, int workExperienceTotal)
    {
        return new EmploymentDto().employerINN(employerINN).employmentStatus(employmentStatus).position(position).salary(salary)
                .workExperienceCurrent(workExperienceCurrent).workExperienceTotal(workExperienceTotal);
    }

    private List<PaymentScheduleElement> createPaymentSchedule() {
        List<PaymentScheduleElement> result = new ArrayList<>();

        result.add(new PaymentScheduleElement().number(1).date(LocalDate.of(2021, 1, 1)).totalPayment(monthlyPayment).debtPayment(new BigDecimal("2000"))
                .interestPayment(new BigDecimal("6000")).remainingDebt(new BigDecimal("42000.00")));
        result.add(new PaymentScheduleElement().number(2).date(LocalDate.of(2021, 2, 1)).totalPayment(monthlyPayment).debtPayment(new BigDecimal("2300"))
                .interestPayment(new BigDecimal("5700")).remainingDebt(new BigDecimal("34000")));
        result.add(new PaymentScheduleElement().number(3).date(LocalDate.of(2021, 3, 1)).totalPayment(monthlyPayment).debtPayment(new BigDecimal("2600"))
                .interestPayment(new BigDecimal("5400")).remainingDebt(new BigDecimal("26000")));
        result.add(new PaymentScheduleElement().number(4).date(LocalDate.of(2021, 4, 1)).totalPayment(monthlyPayment).debtPayment(new BigDecimal("2900"))
                .interestPayment(new BigDecimal("5100")).remainingDebt(new BigDecimal("18000")));
        result.add(new PaymentScheduleElement().number(5).date(LocalDate.of(2021, 5, 1)).totalPayment(monthlyPayment).debtPayment(new BigDecimal("3200"))
                .interestPayment(new BigDecimal("4800")).remainingDebt(new BigDecimal("10000")));
        result.add(new PaymentScheduleElement().number(6).date(LocalDate.of(2021, 6, 1)).totalPayment(monthlyPayment).debtPayment(new BigDecimal("3500"))
                .interestPayment(new BigDecimal("4500")).remainingDebt(new BigDecimal("2000")));
        result.add(new PaymentScheduleElement().number(7).date(LocalDate.of(2021, 6, 1)).totalPayment(monthlyPayment).debtPayment(new BigDecimal("1000"))
                .interestPayment(new BigDecimal("1000")).remainingDebt(BigDecimal.ONE));

        return result;
    }
}