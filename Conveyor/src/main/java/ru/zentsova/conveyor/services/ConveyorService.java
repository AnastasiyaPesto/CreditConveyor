package ru.zentsova.conveyor.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.zentsova.conveyor.model.LoanApplicationRequestDto;
import ru.zentsova.conveyor.model.LoanOfferDto;
import ru.zentsova.conveyor.util.ConveyorUtils;
import ru.zentsova.conveyor.util.validator.LoanApplicationRequestDtoValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class ConveyorService
 */
@Service
public class ConveyorService {
    private long applicationId = 0;

    @Value("${base.rate}")
    private double baseRate;
    @Value("${monthly.insurance.payment}")
    private double monthlyInsurancePayment;
    @Value("${lowering.rate.insurance}")
    private double loweringRateInsurance;
    @Value("${lowering.rate.salary.client}")
    private double loweringRateSalaryClient;

    /**
     * Calculate and get all possible offers
     * @param loanApplicationRequestDto  input loan application request dto
     * @return list of all possible sorted by ascending rate offers
     */
    public List<LoanOfferDto> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto) {
        final List<LoanOfferDto> possibleOffers = new ArrayList<>();
        if (LoanApplicationRequestDtoValidator.validate(loanApplicationRequestDto, true, true, true, false, true, true, true, true, true)) {
            possibleOffers.add(getOffer(loanApplicationRequestDto, false, false));
            possibleOffers.add(getOffer(loanApplicationRequestDto, false, true));
            possibleOffers.add(getOffer(loanApplicationRequestDto, true, false));
            possibleOffers.add(getOffer(loanApplicationRequestDto, true, true));

            return possibleOffers.stream()
                    .sorted(Comparator.comparing(LoanOfferDto::getRate))
                    .collect(Collectors.toList());
        }
        return possibleOffers;
    }

    private LoanOfferDto getOffer(LoanApplicationRequestDto loanApplicationRequestDto, boolean isSalaryClient, boolean isInsuranceEnabled) {
        final LoanOfferDto loanOfferDto = new LoanOfferDto();

        loanOfferDto.setApplicationId(++applicationId);
        loanOfferDto.setIsSalaryClient(isSalaryClient);
        loanOfferDto.setIsInsuranceEnabled(isInsuranceEnabled);
        loanOfferDto.setTerm(loanApplicationRequestDto.getTerm());
        loanOfferDto.setRate(calcRate(isSalaryClient, isInsuranceEnabled));
        loanOfferDto.setRequestedAmount(loanApplicationRequestDto.getAmount());
        loanOfferDto.setTotalAmount(calcTotalAmount(loanApplicationRequestDto.getAmount().doubleValue(), loanApplicationRequestDto.getTerm(), isInsuranceEnabled));
        loanOfferDto.setMonthlyPayment(calcMonthlyPayment(loanOfferDto.getRate(), loanApplicationRequestDto.getTerm(), loanOfferDto.getTotalAmount()));

        return loanOfferDto;
    }

    private BigDecimal calcRate(boolean isSalaryClient, boolean isInsuranceEnabled) {
        BigDecimal rate = new BigDecimal(baseRate).setScale(2, RoundingMode.HALF_UP);
        rate = isSalaryClient ? rate.subtract(new BigDecimal(loweringRateSalaryClient).setScale(2, RoundingMode.HALF_UP)) : rate;
        rate = isInsuranceEnabled ? rate.subtract(new BigDecimal(loweringRateInsurance).setScale(2, RoundingMode.HALF_UP)) : rate;
        return rate;
    }

    private BigDecimal calcTotalAmount(double requestedAmount, int term, boolean isInsuranceEnabled) {
        BigDecimal totalAmount = new BigDecimal(requestedAmount).setScale(2, RoundingMode.HALF_UP);
        return (isInsuranceEnabled ? totalAmount.add(new BigDecimal(term * monthlyInsurancePayment)) : totalAmount);
    }

    private BigDecimal calcMonthlyPayment(BigDecimal rate, int term, BigDecimal totalAmount) {
        return ConveyorUtils.calcMonthlyPayment(rate, term, totalAmount);
    }
}
