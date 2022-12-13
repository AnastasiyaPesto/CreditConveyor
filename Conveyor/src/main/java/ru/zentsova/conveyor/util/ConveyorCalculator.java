package ru.zentsova.conveyor.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.zentsova.conveyor.model.EmploymentDto;
import ru.zentsova.conveyor.model.PaymentScheduleElement;
import ru.zentsova.conveyor.model.ScoringDataDto;
import ru.zentsova.conveyor.util.exceptions.LoanDecisionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Class ConveyorCalculator
 */
@Component
public class ConveyorCalculator {

    private static final BigDecimal MONTHS_PER_YEAR = new BigDecimal("12");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal THREE = new BigDecimal("3");
    private static final BigDecimal FOUR = new BigDecimal("3");
    private static final BigDecimal TWELVE = new BigDecimal("12");
    private static final BigDecimal TWENTY = new BigDecimal("20");
    private static final int MIN_WORK_EXPERIENCE_TOTAL = 6;
    private static final int MIN_WORK_EXPERIENCE_CURRENT = 12;
    private static final int MIN_AGE = 21;
    private static final int MAX_AGE = 60;
    private static final String S_LOAN_DENY_MSG = "Unfortunately, you were denied a loan";

    private long applicationId = 0;
    @Value("${base.rate}")
    private BigDecimal baseRate;
    @Value("${min.rate}")
    private BigDecimal minRate;
    @Value("${max.rate}")
    private BigDecimal maxRate;
    @Value("${insurance.rate}")
    private BigDecimal insuranceRate;
    @Value("${lowering.rate.insurance}")
    private BigDecimal loweringRateInsurance;
    @Value("${lowering.rate.salary.client}")
    private BigDecimal loweringRateSalaryClient;

    public long getApplicationId() {
        return ++applicationId;
    }

    /**
     * Calculate the interest rate taking into account whether the client is a salary client and insurance is included
     * @param isSalaryClient     is client is a salary client
     * @param isInsuranceEnabled is insurance included
     * @return rate
     */
    public BigDecimal calcRate(boolean isSalaryClient, boolean isInsuranceEnabled) {
        BigDecimal rate = baseRate.setScale(2, RoundingMode.HALF_UP);
        rate = isSalaryClient ? rate.subtract(loweringRateSalaryClient.setScale(2, RoundingMode.HALF_UP)) : rate;
        rate = isInsuranceEnabled ? rate.subtract(loweringRateInsurance.setScale(2, RoundingMode.HALF_UP)) : rate;
        return rate;
    }

    /**
     * Calculate exact rate in a scoring
     * @param scoringDataDto scoring data
     * @return rate
     */
    public BigDecimal calcScoreRate(ScoringDataDto scoringDataDto) {
        int age = Period.between(scoringDataDto.getBirthdate(), LocalDate.now()).getYears();
        EmploymentDto employment = scoringDataDto.getEmployment();
        if ((age < MIN_AGE || age > MAX_AGE)
                || EmploymentDto.EmploymentStatusEnum.UNEMPLOYED.equals(employment.getEmploymentStatus())
                || (employment.getSalary().multiply(TWENTY).compareTo(scoringDataDto.getAmount()) < 0)
                || (employment.getWorkExperienceCurrent() < MIN_WORK_EXPERIENCE_TOTAL || employment.getWorkExperienceTotal() < MIN_WORK_EXPERIENCE_CURRENT))
        {
            throw new LoanDecisionException(S_LOAN_DENY_MSG);
        }

        BigDecimal finalRate = calcRate(scoringDataDto.getIsSalaryClient(), scoringDataDto.getIsInsuranceEnabled());
        switch (scoringDataDto.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED:
                finalRate = finalRate.add(BigDecimal.ONE);
                break;
            case BUSINESS_OWNER:
                finalRate = finalRate.add(THREE);
                break;
        }

        if (EmploymentDto.EmploymentStatusEnum.EMPLOYED.equals(scoringDataDto.getEmployment().getEmploymentStatus())) {
            switch (scoringDataDto.getEmployment().getPosition()) {
                case TRAINEE:
                    finalRate = finalRate.add(TWO);
                    break;
                case DEVELOPER:
                case MANAGER:
                    finalRate = finalRate.subtract(TWO);
                    break;
                case TOP_LEVEL_MANAGER:
                    finalRate = finalRate.subtract(FOUR);
                    break;
            }
        }

        switch (scoringDataDto.getMaritalStatus()) {
            case MARRIED:
                finalRate = finalRate.subtract(THREE);
                break;
            case SINGLE:
            case DIVORCED:
                finalRate = finalRate.add(BigDecimal.ONE);
                break;
            case WIDOWER:
                finalRate = finalRate.subtract(BigDecimal.ONE);
                break;
        }

        if (scoringDataDto.getDependentAmount() > 1)
            finalRate = finalRate.add(BigDecimal.ONE);

        if (finalRate.compareTo(maxRate) > 0)
            finalRate = maxRate;
        else if (finalRate.compareTo(minRate) < 0)
            finalRate = minRate;

        return finalRate;
    }

    /**
     * Calculate total loan amount
     * @param requestedAmount    the amount requested by the client
     * @param term               the term requested by the client
     * @param isInsuranceEnabled is insurance included
     * @return total amount
     */
    public BigDecimal calcTotalAmount(BigDecimal requestedAmount, int term, boolean isInsuranceEnabled) {
        BigDecimal totalAmount = requestedAmount.setScale(2, RoundingMode.HALF_UP);
        return (isInsuranceEnabled ? totalAmount.add(calcMonthlyInsurancePayment(requestedAmount).multiply(new BigDecimal(term))) : totalAmount);
    }

    private BigDecimal calcMonthlyInsurancePayment(BigDecimal requestedAmount) {
        return requestedAmount.multiply(insuranceRate).divide(TWELVE, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate monthly payment
     * @param rate           interest rate
     * @param term           loan period
     * @param totalAmount    total amount of the loan includes the requested amount plus the amount of insurance for the entire period
     * @return value of monthly loan payment
     */
    public BigDecimal calcMonthlyPayment(BigDecimal rate, int term, BigDecimal totalAmount) {
        BigDecimal monthlyRate = rate.divide(MONTHS_PER_YEAR, 15, RoundingMode.HALF_UP).divide(ONE_HUNDRED, 15, RoundingMode.HALF_UP);
        BigDecimal divisible = monthlyRate.add(BigDecimal.ONE).pow(term).multiply(monthlyRate);
        BigDecimal divisor = monthlyRate.add(BigDecimal.ONE).pow(term).subtract(BigDecimal.ONE);
        BigDecimal annuityRation = divisible.divide(divisor, 15, RoundingMode.HALF_UP);
        return totalAmount.multiply(annuityRation).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate full cost of the loan
     * @param requestedAmount amount requested by the client
     * @param totalAmount     total loan amount
     * @param termInMonths    term loan in months
     * @return full cost of the loan (psk)
     */
    public BigDecimal calcPskInPercent(BigDecimal requestedAmount, BigDecimal totalAmount, int termInMonths) {
        BigDecimal termInYears = new BigDecimal(termInMonths).divide(TWELVE, 15, RoundingMode.HALF_UP);
        BigDecimal divisible = totalAmount.divide(requestedAmount, 2, RoundingMode.HALF_UP).subtract(BigDecimal.ONE);
        BigDecimal pskInPercent = divisible.divide(termInYears, 15, RoundingMode.HALF_UP).multiply(ONE_HUNDRED);
        return pskInPercent.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate the payment schedule
     * @param totalAmount    total loan amount
     * @param monthlyPayment monthly payment
     * @param ratePercent    interest rate
     * @param term           loan term
     * @return payment schedule
     */
    public List<PaymentScheduleElement> getPaymentSchedule(BigDecimal totalAmount, BigDecimal monthlyPayment, BigDecimal ratePercent, int term) {
        List<PaymentScheduleElement> paymentSchedule = new ArrayList<>();
        YearMonth yearMonthPayment = YearMonth.now();
        LocalDate datePayment = LocalDate.now();
        BigDecimal rate = ratePercent.divide(ONE_HUNDRED, 15, RoundingMode.HALF_UP);
        BigDecimal interestPayment;
        BigDecimal debtPayment;
        BigDecimal remainingDebt = totalAmount;
        for (int num = 1; num <= term; num++) {
            yearMonthPayment = yearMonthPayment.plusMonths(1);
            datePayment = datePayment.plusMonths(1);
            interestPayment = remainingDebt
                    .multiply(rate)
                    .multiply(BigDecimal.valueOf(yearMonthPayment.lengthOfMonth()))
                    .divide(BigDecimal.valueOf(yearMonthPayment.lengthOfYear()), 2, RoundingMode.HALF_UP);
            debtPayment = monthlyPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);
            remainingDebt = remainingDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);
            paymentSchedule.add(
                    new PaymentScheduleElement()
                            .number(num)
                            .date(datePayment)
                            .totalPayment(monthlyPayment)
                            .interestPayment(interestPayment)
                            .debtPayment(debtPayment)
                            .remainingDebt(remainingDebt.compareTo(BigDecimal.ZERO) > 0 ? remainingDebt : BigDecimal.ZERO));
        }
        return paymentSchedule;
    }
}
