package ru.zentsova.application.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.zentsova.application.clients.deal.DealClient;
import ru.zentsova.application.model.LoanApplicationRequestDto;
import ru.zentsova.application.model.LoanOfferDto;
import ru.zentsova.application.util.validator.LoanApplicationRequestDtoValidator;

import java.util.List;

@Service
@Slf4j
public class DealServiceImpl implements DealService {

    private final DealClient dealClient;
    private final LoanApplicationRequestDtoValidator validator;

    @Autowired
    public DealServiceImpl(DealClient dealClient, LoanApplicationRequestDtoValidator validator) {
        this.dealClient = dealClient;
        this.validator = validator;
    }

    public ResponseEntity<List<LoanOfferDto>> executeGetAllPossibleOffers(LoanApplicationRequestDto loanApplicationRequestDto) {
        if (validator.validate(loanApplicationRequestDto, true)) {
            log.info("POST /deal/application to Deal microservice has been sent successfully");
            return dealClient.getAllPossibleOffers(loanApplicationRequestDto);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> executeChooseOneOffer(LoanOfferDto loanOfferDto) {
        log.info("PUT /deal/application/offer to Deal microservice has been sent successfully");
        return dealClient.chooseOneOffer(loanOfferDto);
    }
}