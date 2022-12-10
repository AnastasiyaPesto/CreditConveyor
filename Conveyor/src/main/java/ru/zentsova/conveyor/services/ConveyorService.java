package ru.zentsova.conveyor.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zentsova.conveyor.model.*;
import ru.zentsova.conveyor.util.ConveyorCalculator;
import ru.zentsova.conveyor.util.validator.LoanApplicationRequestDtoValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class ConveyorService
 */
@Service
@RequiredArgsConstructor
public class ConveyorService {

    private final ConveyorCalculator conveyorCalculator;

    /**
     * Calculate and get all possible offers
     * @param loanApplicationRequestDto  input loan application request dto
     * @return list of all possible offers sorted by ascending rate offers
     */
    public List<LoanOfferDto> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto) {
        final List<LoanOfferDto> possibleOffers = new ArrayList<>();
        if (LoanApplicationRequestDtoValidator.validate(loanApplicationRequestDto, true, true, true, false, true, true, true, true, true)) {
            possibleOffers.add(getOffer(loanApplicationRequestDto, false, false));
            possibleOffers.add(getOffer(loanApplicationRequestDto, false, true));
            possibleOffers.add(getOffer(loanApplicationRequestDto, true, false));
            possibleOffers.add(getOffer(loanApplicationRequestDto, true, true));

            return possibleOffers.stream()
                    .sorted(Comparator.comparing(LoanOfferDto::getRate).reversed())
                    .collect(Collectors.toList());
        }
        return possibleOffers;
    }

    private LoanOfferDto getOffer(LoanApplicationRequestDto loanApplicationRequestDto, boolean isSalaryClient, boolean isInsuranceEnabled) {
        LoanOfferDto loanOfferDto = new LoanOfferDto();

        loanOfferDto.setApplicationId(conveyorCalculator.getApplicationId());
        loanOfferDto.setIsSalaryClient(isSalaryClient);
        loanOfferDto.setIsInsuranceEnabled(isInsuranceEnabled);
        loanOfferDto.setTerm(loanApplicationRequestDto.getTerm());
        loanOfferDto.setRate(conveyorCalculator.calcRate(isSalaryClient, isInsuranceEnabled));
        loanOfferDto.setRequestedAmount(loanApplicationRequestDto.getAmount());
        loanOfferDto.setTotalAmount(conveyorCalculator.calcTotalAmount(loanApplicationRequestDto.getAmount(), loanApplicationRequestDto.getTerm(), isInsuranceEnabled));
        loanOfferDto.setMonthlyPayment(conveyorCalculator.calcMonthlyPayment(loanOfferDto.getRate(), loanApplicationRequestDto.getTerm(), loanOfferDto.getTotalAmount()));

        return loanOfferDto;
    }

    public CreditDto getLoanConditions(ScoringDataDto scoringDataDto) {
        CreditDto creditDto = new CreditDto();

        BigDecimal scoreRate = conveyorCalculator.calcScoreRate(scoringDataDto);
        BigDecimal monthlyPayment = conveyorCalculator.calcMonthlyPayment(scoreRate, scoringDataDto.getTerm(), scoringDataDto.getAmount());
        List<PaymentScheduleElement> paymentSchedule = conveyorCalculator.getPaymentSchedule(scoringDataDto.getAmount(), monthlyPayment, scoreRate, scoringDataDto.getTerm());

        BigDecimal requestedAmount = scoringDataDto.getAmount();
        BigDecimal allInterestPayment = paymentSchedule.stream()
                .map(PaymentScheduleElement::getInterestPayment)
                .reduce(BigDecimal.ONE, BigDecimal::add);
        BigDecimal totalAmount = conveyorCalculator
                .calcTotalAmount(scoringDataDto.getAmount(), scoringDataDto.getTerm(), scoringDataDto.getIsInsuranceEnabled())
                .add(allInterestPayment);
        creditDto.setPsk(conveyorCalculator.calcPskInPercent(requestedAmount, totalAmount, scoringDataDto.getTerm()));

        creditDto.setTerm(scoringDataDto.getTerm());
        creditDto.setMonthlyPayment(monthlyPayment);
        creditDto.setRate(scoreRate);
        creditDto.setAmount(totalAmount);
        creditDto.setPaymentSchedule(paymentSchedule);
        creditDto.setIsSalaryClient(scoringDataDto.getIsSalaryClient());
        creditDto.setIsInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled());
        return creditDto;
    }
}
