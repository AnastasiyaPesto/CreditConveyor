package ru.zentsova.conveyor.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.zentsova.conveyor.api.ConveyorApi;
import ru.zentsova.conveyor.model.LoanApplicationRequestDTO;
import ru.zentsova.conveyor.model.LoanOfferDTO;
import ru.zentsova.conveyor.util.validator.LoanApplicationRequestDTOValidator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Class ConveyorController
 */
@RestController
public class ConveyorController implements ConveyorApi {

    @Override
    public ResponseEntity<List<LoanOfferDTO>> getAllPossibleOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        LoanApplicationRequestDTOValidator.validate(loanApplicationRequestDTO);

        LoanOfferDTO loanOfferDTO1 = new LoanOfferDTO().applicationId(1L).term(180).totalAmount(BigDecimal.valueOf(1500000.00))
                .isInsuranceEnabled(true).isSalaryClient(true).rate(BigDecimal.valueOf(10.4)).requestedAmount(BigDecimal.valueOf(1700000.00));
        LoanOfferDTO loanOfferDTO2 = new LoanOfferDTO().applicationId(1L).term(200).totalAmount(BigDecimal.valueOf(1500000.00))
                .isInsuranceEnabled(true).isSalaryClient(false).rate(BigDecimal.valueOf(11.7)).requestedAmount(BigDecimal.valueOf(1700000.00));
        return new ResponseEntity<>(List.of(loanOfferDTO1, loanOfferDTO2), HttpStatus.OK);
    }

}
