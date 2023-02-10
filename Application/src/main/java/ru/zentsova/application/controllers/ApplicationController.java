package ru.zentsova.application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.zentsova.application.api.ApplicationApi;
import ru.zentsova.application.model.LoanApplicationRequestDto;
import ru.zentsova.application.model.LoanOfferDto;
import ru.zentsova.application.services.DealService;

import java.util.List;

@RestController
public class ApplicationController implements ApplicationApi {

    private final DealService dealService;

    @Autowired
    public ApplicationController(DealService dealService) {
        this.dealService = dealService;
    }

    public ResponseEntity<List<LoanOfferDto>> calculateAllPossibleLoanOffers(LoanApplicationRequestDto loanApplicationRequestDto) {
        return dealService.getAllPossibleOffers(loanApplicationRequestDto);
    }

    public ResponseEntity<Void> chooseOneOffer(LoanOfferDto loanOfferDto) {
        return ApplicationApi.super.chooseOneOffer(loanOfferDto);
    }
}