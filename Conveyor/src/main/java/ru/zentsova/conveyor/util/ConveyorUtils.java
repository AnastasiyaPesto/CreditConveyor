package ru.zentsova.conveyor.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class ConveyorUtils
 * Helps to make calculations to calculate the parameters of the loan
 */
public class ConveyorUtils {
    private static final BigDecimal MONTHS_PER_YEAR = new BigDecimal("12");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    /**
     * Calculate monthly payment
     * @param rate           interest rate
     * @param term           loan period
     * @param totalAmount    total amount of the loan includes the requested amount plus the amount of insurance for the entire period
     * @return value of monthly loan payment
     */
    public static BigDecimal calcMonthlyPayment(BigDecimal rate, int term, BigDecimal totalAmount) {
        BigDecimal monthlyRate = rate.divide(MONTHS_PER_YEAR, 15, RoundingMode.HALF_UP).divide(ONE_HUNDRED, 15, RoundingMode.HALF_UP);
        BigDecimal divisible = monthlyRate.add(BigDecimal.ONE).pow(term).multiply(monthlyRate);
        BigDecimal divisor = monthlyRate.add(BigDecimal.ONE).pow(term).subtract(BigDecimal.ONE);
        BigDecimal annuityRation = divisible.divide(divisor, 15, RoundingMode.HALF_UP);
        return totalAmount.multiply(annuityRation).setScale(2, RoundingMode.HALF_UP);
    }
}
