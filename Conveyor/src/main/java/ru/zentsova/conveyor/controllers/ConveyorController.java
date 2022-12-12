package ru.zentsova.conveyor.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.zentsova.conveyor.api.ConveyorApi;
import ru.zentsova.conveyor.model.CreditDto;
import ru.zentsova.conveyor.model.LoanApplicationRequestDto;
import ru.zentsova.conveyor.model.LoanOfferDto;
import ru.zentsova.conveyor.model.ScoringDataDto;
import ru.zentsova.conveyor.services.impl.ConveyorServiceImpl;

import java.util.List;

/**
 * Class ConveyorController
 */
@RestController
@RequiredArgsConstructor
public class ConveyorController implements ConveyorApi {

    private final ConveyorServiceImpl conveyorService;

    @Override
    public ResponseEntity<List<LoanOfferDto>> getAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDTO) {
        List<LoanOfferDto> offers = conveyorService.getAllPossibleOffers(loanApplicationRequestDTO);
        return new ResponseEntity<>(offers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CreditDto> getFullCalculatedParameters(ScoringDataDto scoringDataDto) {
        return new ResponseEntity<>(conveyorService.getLoanConditions(scoringDataDto), HttpStatus.OK);
    }
}
