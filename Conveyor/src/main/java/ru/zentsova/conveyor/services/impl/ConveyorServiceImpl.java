package ru.zentsova.conveyor.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zentsova.conveyor.model.*;
import ru.zentsova.conveyor.services.ConveyorService;
import ru.zentsova.conveyor.util.ConveyorCalculator;
import ru.zentsova.conveyor.util.exceptions.ApplicationException;
import ru.zentsova.conveyor.util.validator.LoanApplicationRequestDtoValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service of getting pre and final loan conditions
 */
@Service
@RequiredArgsConstructor
public class ConveyorServiceImpl implements ConveyorService {

    private final ConveyorCalculator conveyorCalculator;
    private final LoanApplicationRequestDtoValidator loanApplicationRequestDtoValidator;

    /**
     * Calculate and get all possible offers
     * @param loanApplicationRequestDto  input loan application request dto
     * @return list of all possible offers sorted by descending rate offers
     * @throws ApplicationException if loanApplicationRequestDto is invalid
     */
    public List<LoanOfferDto> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto) throws ApplicationException {
        final List<LoanOfferDto> possibleOffers = new ArrayList<>();
        if (loanApplicationRequestDtoValidator.validate(loanApplicationRequestDto, true)) {
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

    /**
     * Calculate and get loan conditions
     * @param scoringDataDto input scoring data dto
     * @return credit dto
     */
    public CreditDto getLoanConditions(ScoringDataDto scoringDataDto) {
        CreditDto creditDto = new CreditDto();

        BigDecimal scoreRate = conveyorCalculator.calcScoreRate(scoringDataDto);
        BigDecimal monthlyPayment = conveyorCalculator.calcMonthlyPayment(scoreRate, scoringDataDto.getTerm(), scoringDataDto.getAmount());
        List<PaymentScheduleElement> paymentSchedule = conveyorCalculator.getPaymentSchedule(scoringDataDto.getAmount(), monthlyPayment, scoreRate, scoringDataDto.getTerm());

        BigDecimal requestedAmount = scoringDataDto.getAmount();
        BigDecimal allInterestPayment = paymentSchedule.stream()
                .map(PaymentScheduleElement::getInterestPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
