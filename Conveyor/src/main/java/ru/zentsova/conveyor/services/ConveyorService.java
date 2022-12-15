package ru.zentsova.conveyor.services;

import ru.zentsova.conveyor.model.CreditDto;
import ru.zentsova.conveyor.model.LoanApplicationRequestDto;
import ru.zentsova.conveyor.model.LoanOfferDto;
import ru.zentsova.conveyor.model.ScoringDataDto;

import java.util.List;

/**
 * Class ConveyorService
 */
public interface ConveyorService {
    CreditDto getLoanConditions(ScoringDataDto scoringDataDto);
    List<LoanOfferDto> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto);
}